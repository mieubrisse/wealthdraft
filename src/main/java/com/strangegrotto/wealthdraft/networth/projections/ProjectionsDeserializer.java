package com.strangegrotto.wealthdraft.networth.projections;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import com.strangegrotto.wealthdraft.errors.Gerr;
import com.strangegrotto.wealthdraft.errors.ValOrGerr;
import com.strangegrotto.wealthdraft.networth.Asset;
import com.strangegrotto.wealthdraft.networth.AssetChange;
import com.strangegrotto.wealthdraft.networth.AssetType;
import jdk.vm.ci.meta.Local;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProjectionsDeserializer extends JsonDeserializer<Projections> {
    private static class RawjProjections {
        @JsonProperty
        public double defaultAnnualGrowth;

        @JsonProperty
        public Map<String, RawProjectionScenario> scenarios;
    }

    private static class RawProjectionScenario {
        @JsonProperty
        public String name;

        @JsonProperty
        public Map<RelativeLocalDate, Map<String, Map<String, String>>> changes;
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

        ImmutableProjections.Builder resultBuilder = ImmutableProjections.builder()
                .defaultAnnualGrowth(rawjProjections.defaultAnnualGrowth);

        Map<String, RawProjectionScenario> rawScenarios = rawjProjections.scenarios;
        for (String scenarioId : rawScenarios.keySet()) {
            RawProjectionScenario rawScenario = rawScenarios.get(scenarioId);

            ValOrGerr<ProjectionScenario> parsedScenarioOrErr = parseProjectionScenario(scenarioId, rawScenario, this.assets, mapper);
            resultBuilder.putScenarios(scenarioId, parsedScenarioOrErr);
        }
        return resultBuilder.build();
    }

    /**
     * Parses & validates the JSON for a projection scenario into the type of object that we need
     */
    private static ValOrGerr<ProjectionScenario> parseProjectionScenario(
            String scenarioId,
            RawProjectionScenario rawScenario,
            Map<String, Asset> assets,
            ObjectMapper mapper) {
        // Flips the order of the map keys from date -> (assetId -> change) to
        //  assetId ->  (date -> change), since the first is more sensible for the user to
        //  specify in YAML but the latter is what we need in code
        Map<String, ListMultimap<LocalDate, AssetChange<?>>> parsedAssetChanges = new HashMap<>();
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

                ListMultimap<LocalDate, AssetChange<?>> parsedChangesForAsset = parsedAssetChanges.getOrDefault(
                        assetId,
                        MultimapBuilder.treeKeys().arrayListValues().build()
                );
                parsedChangesForAsset.put(actualDate, parsedAssetChange);
                parsedAssetChanges.put(assetId, parsedChangesForAsset);
            }
        }

        return ValOrGerr.val(new ProjectionScenario(
                rawScenario.name,
                assets,
                parsedAssetChanges
        ));
    }
}
