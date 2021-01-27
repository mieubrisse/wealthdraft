package com.strangegrotto.wealthdraft.backend.projections.impl;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.strangegrotto.wealthdraft.AbstractYmlBackedStoreFactory;
import com.strangegrotto.wealthdraft.backend.assets.api.AssetsStore;
import com.strangegrotto.wealthdraft.backend.projections.api.types.AssetChange;
import com.strangegrotto.wealthdraft.backend.projections.api.types.ProjectionScenario;
import com.strangegrotto.wealthdraft.backend.projections.impl.temporal.SerAssetParameterChangeDeserializer;
import com.strangegrotto.wealthdraft.backend.projections.impl.temporal.SerAssetParameterChange;
import com.strangegrotto.wealthdraft.errors.ValOrGerr;

import java.time.LocalDate;
import java.util.*;

public class SimpleProjectionsStoreFactory extends AbstractYmlBackedStoreFactory<
        SerProjections,
        SerProjections,
        SimpleProjectionsStore> {

    private final AssetsStore assetsStore;

    public SimpleProjectionsStoreFactory(ObjectMapper baseMapper, AssetsStore assetsStore) {
        super(baseMapper);
        this.assetsStore = assetsStore;
    }

    @Override
    protected void configureMapper(ObjectMapper mapper) {
        var module = new SimpleModule();
        module.addDeserializer(SerProjections.class, new SerProjectionsDeserializer(this.assetsStore));
        module.addDeserializer(SerAssetParameterChange.class, new SerAssetParameterChangeDeserializer());
        mapper.registerModule(module);
    }

    @Override
    protected JavaType getDeserializationType(TypeFactory typeFactory) {
        return typeFactory.constructType(SerProjections.class);
    }

    @Override
    protected SerProjections postprocess(SerProjections deserialized) {
        var notUnrolledScenarios = deserialized.getScenarios();
        Map<String, SerProjectionScenario> unrolledScenarios = new HashMap<>();
        for (String scenarioId : notUnrolledScenarios.keySet()) {
            var unrolledScenarioOrErr = unrollScenarioAssetChanges(scenarioId, notUnrolledScenarios);
            // TODO get rid of this Gerr silliness
            if (unrolledScenarioOrErr.hasGerr()) {
                throw new IllegalStateException(Strings.lenientFormat(
                        "An error occurred unrolling scenario asset changes for scenario '%s'",
                        scenarioId
                ));
            }
            unrolledScenarios.put(scenarioId, unrolledScenarioOrErr.getVal());
        }
        return ImmSerProjections.of(
                deserialized.getDefaultAnnualGrowth(),
                unrolledScenarios
        );
    }

    @Override
    protected void validate(SerProjections projections) {
        var scenariosMap = projections.getScenarios();
        for (var projectionEntry : scenariosMap.entrySet()) {
            var scenarioId = projectionEntry.getKey();
            var scenario = projectionEntry.getValue();

            var scenarioAssetChanges = scenario.getAssetChanges();
            for (var changesForDateEntry : scenarioAssetChanges.entrySet()) {
                var date = changesForDateEntry.getKey();

                Preconditions.checkState(
                        date.isAfter(LocalDate.now()) || date.isEqual(LocalDate.now()),
                        "Projection scenario %s cannot be used because it has asset changes in the past, on date %s",
                        scenarioId,
                        date
                );
            }
        }
    }

    @Override
    protected SimpleProjectionsStore buildResult(SerProjections projections) {
        var castedMap = Map.<String, ProjectionScenario>copyOf(projections.getScenarios());
        return new SimpleProjectionsStore(castedMap);
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
            SerProjectionScenario scenarioToVisit = notUnrolledScenarios.get(scenarioIdToVisit);

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

        SerProjectionScenario notUnrolledScenario = notUnrolledScenarios.get(scenarioId);
        SerProjectionScenario unrolledScenario = ImmSerProjectionScenario.of(
                notUnrolledScenario.getName(),
                notUnrolledScenario.getBase(),
                unrolledAssetChanges
        );
        return ValOrGerr.val(unrolledScenario);
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
