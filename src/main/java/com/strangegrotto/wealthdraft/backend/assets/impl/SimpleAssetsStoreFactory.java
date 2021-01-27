package com.strangegrotto.wealthdraft.backend.assets.impl;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.strangegrotto.wealthdraft.AbstractYmlBackedStoreFactory;
import com.strangegrotto.wealthdraft.backend.assets.api.types.Asset;
import com.strangegrotto.wealthdraft.backend.tags.custom.api.CustomTagStore;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

public class SimpleAssetsStoreFactory extends AbstractYmlBackedStoreFactory<
        Map<String, SerAsset>,
        Map<String, SerAsset>,
        SimpleAssetsStore> {
    private final CustomTagStore customTagStore;

    public SimpleAssetsStoreFactory(ObjectMapper baseMapper, CustomTagStore customTagStore) {
        super(baseMapper);
        this.customTagStore = customTagStore;
    }

    @Override
    protected void configureMapper(ObjectMapper mapper) {

    }

    @Override
    protected JavaType getDeserializationType(TypeFactory typeFactory) {
        return typeFactory.constructMapType(HashMap.class, String.class, SerAsset.class);
    }

    @Override
    protected Map<String, SerAsset> postprocess(Map<String, SerAsset> deserialized) {
        var customTags = this.customTagStore.getTags();
        var customTagDefaultValues = customTags.entrySet().stream()
                .filter(entry -> entry.getValue().getDefaultValue().isPresent())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().getDefaultValue().get()
                ));

        // Use default values to populate missing tags
        var newAssetsBuilder = ImmutableMap.<String, SerAsset>builder();
        for (var assetEntry : deserialized.entrySet()) {
            var assetId = assetEntry.getKey();
            var asset = assetEntry.getValue();
            var assetCustomTags = asset.getCustomTags();

            var newTags = new HashMap<String, String>();
            newTags.putAll(customTagDefaultValues);  // Default tags first, so they can get overridden
            newTags.putAll(assetCustomTags);
            var newAsset = ImmSerAsset.copyOf(asset).withCustomTags(newTags);

            newAssetsBuilder.put(assetId, newAsset);
        }
        return newAssetsBuilder.build();
    }

    @Override
    protected void validate(Map<String, SerAsset> postprocessed) {
        var customTags = this.customTagStore.getTags();
        var requiredCustomTags = customTags.entrySet().stream()
                .filter(entry -> entry.getValue().isRequired())
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        for (var assetEntry : postprocessed.entrySet()) {
            var assetId = assetEntry.getKey();
            var asset = assetEntry.getValue();

            var customTagsForAsset = asset.getCustomTags();
            var requiredTagsNotSeen = new HashSet<>(requiredCustomTags);
            for (var tagEntry : customTagsForAsset.entrySet()) {
                var customTagName = tagEntry.getKey();
                var customTagValue = tagEntry.getValue();

                Preconditions.checkState(
                        customTags.containsKey(customTagName),
                        "Custom tag '%s' on asset '%s' doesn't match any defined custom tag",
                        customTagName,
                        assetId
                );

                requiredTagsNotSeen.remove(customTagName);

                var customTagDefinition = customTags.get(customTagName);
                var allowedTagValues = customTagDefinition.getAllowedValues();
                if (allowedTagValues.size() > 0) {
                    Preconditions.checkState(
                            allowedTagValues.contains(customTagValue),
                            "Tag '%s' for asset '%s' has a value not in the allowed values list for the tag",
                            customTagName,
                            customTagValue
                    );
                }
            }

            Preconditions.checkState(
                    requiredTagsNotSeen.size() == 0,
                    "Asset '%s' doesn't have required tags %s",
                    assetId,
                    requiredTagsNotSeen.toString());
        }
    }

    @Override
    protected SimpleAssetsStore buildResult(Map<String, SerAsset> stringAssetMap) {
        return new SimpleAssetsStore(stringAssetMap);
    }
}
