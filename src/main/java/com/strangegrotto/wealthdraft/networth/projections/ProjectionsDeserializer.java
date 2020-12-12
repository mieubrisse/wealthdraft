package com.strangegrotto.wealthdraft.networth.projections;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.strangegrotto.wealthdraft.errors.Gerr;
import com.strangegrotto.wealthdraft.errors.ValOrGerr;
import com.strangegrotto.wealthdraft.networth.Asset;
import com.strangegrotto.wealthdraft.networth.AssetType;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class ProjectionsDeserializer extends JsonDeserializer<Projections> {
    private static class RawProjectionScenario {
        @JsonProperty
        public String name;

        @JsonProperty
        public Optional<String> base;

        @JsonProperty
        public Map<RelativeLocalDate, Map<String, Map<String, String>>> changes;
    }

    private static class RawjProjections {
        @JsonProperty
        public double defaultAnnualGrowth;

        @JsonProperty
        public Map<String, RawProjectionScenario> scenarios;
    }

    private static class NotUnrolledParsedScenario {
        public final String name;
        public final Optional<String> base;
        public final Map<String, Asset> assets;
        public final Map<LocalDate, Map<String, AssetChange<?>>> assetChanges;

        private NotUnrolledParsedScenario(String name, Optional<String> base, Map<String, Asset> assets, Map<LocalDate, Map<String, AssetChange<?>>> assetChanges) {
            this.name = name;
            this.base = base;
            this.assets = assets;
            this.assetChanges = assetChanges;
        }
    }

    private final Map<String, Asset> assets;

    public ProjectionsDeserializer(Map<String, Asset> assets) {
        this.assets = assets;
    }

    @Override
    public Projections deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        RawjProjections rawjProjections = p.readValueAs(RawjProjections.class);

        // It's VERY unclear to me how to call the needed convertValue function without doing
        //  this hacky cast
        ObjectMapper mapper = (ObjectMapper) p.getCodec();

        Map<String, RawProjectionScenario> rawScenarios = rawjProjections.scenarios;
        Map<String, ValOrGerr<NotUnrolledParsedScenario>> notUnrolledParsedScenarios = new HashMap<>();
        for (String scenarioId : rawScenarios.keySet()) {
            RawProjectionScenario rawScenario = rawScenarios.get(scenarioId);

            ValOrGerr<NotUnrolledParsedScenario> parsedScenarioOrErr = parseProjectionScenario(scenarioId, rawScenario, this.assets, mapper);
            notUnrolledParsedScenarios.put(scenarioId, parsedScenarioOrErr);
        }

        Map<String, ValOrGerr<ProjectionScenario>> unrolledParsedScenarios = new HashMap<>();
        for (String scenarioId : notUnrolledParsedScenarios.keySet()) {
            ValOrGerr<ProjectionScenario> unrolledScenarioOrErr = unrollScenarioAssetChanges(scenarioId, notUnrolledParsedScenarios);
            unrolledParsedScenarios.put(scenarioId, unrolledScenarioOrErr);
        }

        Projections result = ImmutableProjections.builder()
                .defaultAnnualGrowth(rawjProjections.defaultAnnualGrowth)
                .putAllScenarios(unrolledParsedScenarios)
                .build();
        return result;
    }

    /**
     * Parses & validates the JSON for a projection scenario into the type of object that we need
     */
    private static ValOrGerr<NotUnrolledParsedScenario> parseProjectionScenario(
            String scenarioId,
            RawProjectionScenario rawScenario,
            Map<String, Asset> assets,
            ObjectMapper mapper) {
        Map<LocalDate, Map<String, AssetChange<?>>> parsedAssetChanges = new HashMap<>();
        for (RelativeLocalDate relativeDate : rawScenario.changes.keySet()) {
            Map<String, Map<String, String>> unparsedAssetChangesOnDate = rawScenario.changes.get(relativeDate);

            LocalDate actualDate = relativeDate.getLocalDate();

            if (actualDate.isBefore(LocalDate.now())) {
                return ValOrGerr.newGerr(
                        "Projection scenario {} cannot be used because it has asset changes in the past, on date {}",
                        scenarioId,
                        actualDate
                );
            }

            for (String assetId : unparsedAssetChangesOnDate.keySet()) {
                Map<String, String> unparsedAssetChange = unparsedAssetChangesOnDate.get(assetId);

                if (!assets.containsKey(assetId)) {
                    return ValOrGerr.newGerr(
                            "Projection {} defines a change for asset {} but this asset doesn't exist",
                            scenarioId,
                            assetId
                    );
                }

                Asset referencedAsset = assets.get(assetId);
                AssetType referencedAssetType = referencedAsset.getType();
                AssetChange<?> parsedAssetChange;
                try {
                    parsedAssetChange = mapper.convertValue(unparsedAssetChange, referencedAssetType.getChangeType());
                } catch (IllegalArgumentException e) {
                    return ValOrGerr.propGerr(
                            Gerr.newGerr(e.getMessage()),
                            "An error occurred parsing the asset change"
                    );
                }

                Map<String, AssetChange<?>> assetChangesForDate = parsedAssetChanges.getOrDefault(
                        actualDate,
                        new HashMap<>()
                );
                // TODO support changing an asset multiple times in a given day
                if (assetChangesForDate.containsKey(assetId)) {
                    return ValOrGerr.newGerr(
                            "Scenario {} has more than one change for asset {} on date {}",
                            scenarioId,
                            assetId,
                            actualDate
                    );
                }
                assetChangesForDate.put(assetId, parsedAssetChange);
                parsedAssetChanges.put(actualDate, assetChangesForDate);
            }
        }

        return ValOrGerr.val(new NotUnrolledParsedScenario(
                rawScenario.name,
                rawScenario.base,
                assets,
                parsedAssetChanges
        ));
    }

    // TODO Write tests for this!!
    /**
     * For scenarios that are based on other scenarios, recursively unrolls the asset changes all the way
     *  through the dependency tree.
     */
    @VisibleForTesting
    static ValOrGerr<ProjectionScenario> unrollScenarioAssetChanges(
            String scenarioId,
            Map<String, ValOrGerr<NotUnrolledParsedScenario>> notUnrolledScenarios) {
        // We can't unroll a scenario if an error occurred trying to parse it
        ValOrGerr<NotUnrolledParsedScenario> notUnrolledScenarioOrErr = notUnrolledScenarios.get(scenarioId);
        if (notUnrolledScenarioOrErr.hasGerr()) {
            return ValOrGerr.propGerr(
                    notUnrolledScenarioOrErr.getGerr(),
                    "Couldn't unroll dependencies for scenario '{}' because an error occurred during parsing",
                    scenarioId
            );
        }

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
        Map<LocalDate, Map<String, AssetChange<?>>> unrolledAssetChanges = new HashMap<>();
        for (String scenarioIdToVisit : scenarioIdsToVisit) {
            ValOrGerr<NotUnrolledParsedScenario> scenarioToVisitOrErr = notUnrolledScenarios.get(scenarioIdToVisit);
            Preconditions.checkState(
                    !scenarioToVisitOrErr.hasGerr(),
                    "Our list of scenarios to visit somehow contains an errored scenario; this is a code bug, " +
                            "since we shouldn't have even received the list of scenarios to visit if any of the " +
                            "dependency scenarios had parse errors");
            NotUnrolledParsedScenario scenarioToVisit = scenarioToVisitOrErr.getVal();

            Map<LocalDate, Map<String, AssetChange<?>>> scenarioAssetChanges = scenarioToVisit.assetChanges;
            for (LocalDate date : scenarioAssetChanges.keySet()) {
                Map<String, AssetChange<?>> unrolledChangesForDate = unrolledAssetChanges.getOrDefault(
                        date,
                        new HashMap<>()
                );

                Map<String, AssetChange<?>> scenarioChangesForDate = scenarioAssetChanges.get(date);
                for (String assetId : scenarioChangesForDate.keySet()) {
                    if (unrolledChangesForDate.containsKey(date)) {
                        return ValOrGerr.newGerr(
                                "Scenario {} depends on scenario {}, which results in a duplicate change for {} on date {}",
                                scenarioId,
                                scenarioIdToVisit,
                                assetId,
                                date
                        );
                    }
                    AssetChange<?> assetChange = scenarioChangesForDate.get(assetId);
                    unrolledChangesForDate.put(assetId, assetChange);
                }
                unrolledAssetChanges.put(date, unrolledChangesForDate);
            }
        }

        NotUnrolledParsedScenario notUnrolledScenario = notUnrolledScenarioOrErr.getVal();
        ProjectionScenario result = ImmutableProjectionScenario.builder()
                .name(notUnrolledScenario.name)
                .base(notUnrolledScenario.base)
                .changes(unrolledAssetChanges)
                .build();
        return ValOrGerr.val(result);
    }

    private static ValOrGerr<Stack<String>> getScenarioIdsToVisit(String scenarioId, Map<String, ValOrGerr<NotUnrolledParsedScenario>> notUnrolledScenarios) {
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

            ValOrGerr<NotUnrolledParsedScenario> notUnrolledBaseScenarioOrErr = notUnrolledScenarios.get(baseId);
            if (notUnrolledBaseScenarioOrErr.hasGerr()) {
                return ValOrGerr.newGerr(
                        "Could not get list of dependency scenarios for scenario '{}'; it depends on scenario '{}' which has a parsing error",
                        scenarioId,
                        baseId
                );
            }
            NotUnrolledParsedScenario notUnrolledBaseScenario = notUnrolledBaseScenarioOrErr.getVal();

            scenarioIdsToVisit.push(baseId);
            visitedScenarioIds.add(baseId);
            baseIdOpt = notUnrolledBaseScenario.base;
        }
        return ValOrGerr.val(scenarioIdsToVisit);
    }
}
