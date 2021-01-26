package com.strangegrotto.wealthdraft.backend.assets.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import com.strangegrotto.wealthdraft.backend.tags.custom.api.types.CustomTagDefinition;
import org.immutables.value.Value;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

// TODO delete
class AssetDefinitionsDeserializer extends JsonDeserializer<AssetDefinitions> {
    @WealthdraftImmutableStyle
    @Value.Immutable
    @JsonDeserialize(as = ImmRawAssetDefinitions.class)
    // Package-private because private isn't allowed with immutables, but we want to minimize exposure
    interface RawAssetDefinitions {
        @JsonProperty("assets")
        Map<String, SerAsset> getAssets();

        @JsonProperty("customTags")
        Map<String, CustomTagDefinition> getCustomTags();
    }

    @Override
    public AssetDefinitions deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        var rawAssetDefs = p.readValueAs(RawAssetDefinitions.class);

        var customTagDefaultValues = rawAssetDefs.getCustomTags().entrySet().stream()
                .filter(entry -> entry.getValue().getDefaultValue().isPresent())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().getDefaultValue().get()
                ));

        // Use default values to populate missing tags
        var newAssets = new HashMap<String, SerAsset>();
        for (var assetEntry : rawAssetDefs.getAssets().entrySet()) {
            var assetId = assetEntry.getKey();
            var asset = assetEntry.getValue();
            var assetCustomTags = asset.getCustomTags();

            var newTags = new HashMap<>(customTagDefaultValues);
            newTags.putAll(assetCustomTags);
            var newAsset = ImmSerAsset.copyOf(asset).withCustomTags(newTags);

            newAssets.put(assetId, newAsset);
        }

        return ImmAssetDefinitions.builder()
                .putAllAssets(newAssets)
                .putAllCustomTags(rawAssetDefs.getCustomTags())
                .build();
    }
}
