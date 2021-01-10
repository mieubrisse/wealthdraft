package com.strangegrotto.wealthdraft.assets.definition;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.immutables.value.Value;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class AssetDefinitionsDeserializer extends JsonDeserializer<AssetDefinitions> {
    private static class RawAssetDefinitions {
        @JsonProperty("assets")
        private Map<String, Asset<?, ?>> assets;

        @JsonProperty("customTags")
        private Map<String, CustomTagDefinition> customTags;
    }

    @Override
    public AssetDefinitions deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        var rawAssetDefs = p.readValueAs(RawAssetDefinitions.class);

        var customTagDefaultValues = rawAssetDefs.customTags.entrySet().stream()
                .filter(entry -> entry.getValue().getDefaultValue().isPresent())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().getDefaultValue().get()
                ));

        // Use default values to populate missing tags
        for (var assetEntry : rawAssetDefs.assets.entrySet()) {
            var assetId = assetEntry.getKey();
            var asset = assetEntry.getValue();
            var assetTags = asset.getCustomTags();

            var newTags = new HashMap<>(asset.getCustomTags());
            for (var tagDefaultValueEntry : customTagDefaultValues.entrySet()) {
                if ()

            }
        }
    }
}
