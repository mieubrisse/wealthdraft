package com.strangegrotto.wealthdraft.projections.impl;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.common.base.Preconditions;
import com.strangegrotto.wealthdraft.AbstractYmlBackedStoreFactory;
import com.strangegrotto.wealthdraft.errors.ValOrGerr;
import com.strangegrotto.wealthdraft.networth.projections.ImmProjectionScenario;
import com.strangegrotto.wealthdraft.projections.api.types.ProjectionScenario;
import com.strangegrotto.wealthdraft.projections.impl.temporal.AssetChange;

import java.time.LocalDate;
import java.util.*;

public class SimpleProjectionsStoreFactory implements AbstractYmlBackedStoreFactory<
        SerProjections,
        SerProjections,
        SimpleProjectionsStore> {

    @Override
    protected void configureMapper(ObjectMapper mapper) {

    }

    @Override
    protected JavaType getDeserializationType(TypeFactory typeFactory) {
        return null;
    }

    @Override
    protected SerProjections postprocess(SerProjections projections) {
        var notUnrolledScenarios = projections.getScenarios();
        Map<String, ProjectionScenario> unrolledScenarios = new HashMap<>();
        for (String scenarioId : notUnrolledScenarios.keySet()) {
            var unrolledScenario = unrollScenarioAssetChanges(scenarioId, notUnrolledScenarios);
            unrolledScenarios.put(scenarioId, unrolledScenario);
        }
    }

    @Override
    protected void validate(SerProjections projections) {

    }

    @Override
    protected SimpleProjectionsStore buildResult(SerProjections projections) {
        return null;
    }

    /**
     * For scenarios that are based on other scenarios, recursively unrolls the asset changes all the way
     *  through the dependency tree.
     */
    private static ValOrGerr<SerProjectionScenario> unrollScenarioAssetChanges(
            String scenarioId,
            Map<String, SerProjectionScenario> notUnrolledScenarios) {
        ValOrGerr<Stack<String>> scenarioIdsToVisitOrErr = getScenarioIdsToVisit(scenarioId, notUnrolledScenarios);
        if (scenarioIdsToVisitOrErr.hasGerr()) {
            return ValOrGerr.propGerr(
                    scenarioIdsToVisitOrErr.getGerr(),
                    "An error occurred getting the list of dependency scenarios for scenario '{}'",
                    scenarioId
            );
        }
        Stack<String> scenarioIdsToVisit = scenarioIdsToVisitOrErr.getVal();

        // Now that we have the full list of dependency scenarios to visit, in order, loop through them and build a list
        //  asset changes that we'll return
        var unrolledAssetChanges = new TreeMap<LocalDate, Map<String, AssetChange>>();
        for (String scenarioIdToVisit : scenarioIdsToVisit) {
            ValOrGerr<SerProjectionScenario> scenarioToVisitOrErr = notUnrolledScenarios.get(scenarioIdToVisit);
            Preconditions.checkState(
                    !scenarioToVisitOrErr.hasGerr(),
                    "Our list of scenarios to visit somehow contains an errored scenario; this is a code bug, " +
                            "since we shouldn't have even received the list of scenarios to visit if any of the " +
                            "dependency scenarios had parse errors");
            SerProjectionScenario scenarioToVisit = scenarioToVisitOrErr.getVal();

            Map<LocalDate, Map<String, AssetChange>> scenarioAssetChanges = scenarioToVisit.getAssetChanges();
            for (var changesForDateEntry : scenarioAssetChanges.entrySet()) {
                var date = changesForDateEntry.getKey();
                var scenarioChangesForDate = changesForDateEntry.getValue();

                Map<String, AssetChange> unrolledChangesForDate = unrolledAssetChanges.getOrDefault(
                        date,
                        new HashMap<>()
                );
                for (var assetChangeEntry : scenarioChangesForDate.entrySet()) {
                    var assetId = assetChangeEntry.getKey();
                    var assetChange = assetChangeEntry.getValue();

                    if (unrolledChangesForDate.containsKey(assetId)) {
                        return ValOrGerr.newGerr(
                                "Scenario {} depends on scenario {}, which results in a duplicate change for {} on date {}",
                                scenarioId,
                                scenarioIdToVisit,
                                assetId,
                                date
                        );
                    }
                    unrolledChangesForDate.put(assetId, assetChange);
                }
                unrolledAssetChanges.put(date, unrolledChangesForDate);
            }
        }

        SerProjectionScenario notUnrolledScenario = notUnrolledScenarioOrErr.getVal();
        SerProjectionScenario result = ImmProjectionScenario.of(
                notUnrolledScenario.name,
                unrolledAssetChanges
        ).withBase(notUnrolledScenario.base);
        return ValOrGerr.val(result);
    }

    private static ValOrGerr<Stack<String>> getScenarioIdsToVisit(String scenarioId, Map<String, SerProjectionScenario> notUnrolledScenarios) {
        Optional<String> baseIdOpt = Optional.of(scenarioId);
        Stack<String> scenarioIdsToVisit = new Stack<>();
        Set<String> visitedScenarioIds = new HashSet<>();
        while (baseIdOpt.isPresent()) {
            String baseId = baseIdOpt.get();
            if (visitedScenarioIds.contains(baseId)) {
                return ValOrGerr.newGerr(
                        "Could not get list of dependency scenarios for scenario {} due to a dependency cycle; dependency {} is visited twice",
                        scenarioId,
                        baseId
                );
            }

            if (!notUnrolledScenarios.containsKey(baseId)) {
                return ValOrGerr.newGerr(
                        "Could not get list of dependency scenarios for scenario {} due to a dependency on a nonexistent scenario ID {}",
                        scenarioId,
                        baseId
                );
            }

            SerProjectionScenario notUnrolledBaseScenario = notUnrolledScenarios.get(baseId);

            scenarioIdsToVisit.push(baseId);
            visitedScenarioIds.add(baseId);
            baseIdOpt = notUnrolledBaseScenario.getBase();
        }
        return ValOrGerr.val(scenarioIdsToVisit);
    }
}
