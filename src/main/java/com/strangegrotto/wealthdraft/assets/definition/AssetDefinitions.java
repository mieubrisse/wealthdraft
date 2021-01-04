package com.strangegrotto.wealthdraft.assets.definition;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import org.immutables.value.Value;

import java.util.HashSet;
import java.util.Map;

@WealthdraftImmutableStyle
@Value.Immutable
@JsonDeserialize(as = ImmAssetDefinitions.class)
public interface AssetDefinitions {
    @Value.Parameter
    Map<String, Asset<?, ?>> getAssets();

    Map<String, CustomTagDefinition> getCustomTags();

    @Value.Check
    default void check() {
        var customTagDefinitions = getCustomTags();

        var requiredCustomTags = new HashSet<String>();
        for (var customTagEntry : customTagDefinitions.entrySet()) {
            var customTagName = customTagEntry.getKey();
            var customTagDefinition = customTagEntry.getValue();

            Preconditions.checkState(
                    !IntrinsicAssetTag.getAllNames().contains(customTagName),
                    "Custom tag '%s' collides with an intrinsic tag",
                    customTagName
            );
            if (customTagDefinition.isRequired()) {
                requiredCustomTags.add(customTagName);
            }
        }

        var assets = getAssets();
        for (var assetEntry : assets.entrySet()) {
            var assetId = assetEntry.getKey();
            var asset = assetEntry.getValue();
            var customTagsForAsset = asset.getTags();
            var requiredTagsNotSeen = new HashSet<>(requiredCustomTags);
            for (var tagEntry : customTagsForAsset.entrySet()) {
                var customTagName = tagEntry.getKey();
                var customTagValue = tagEntry.getValue();

                Preconditions.checkState(
                        customTagDefinitions.containsKey(customTagName),
                        "Custom tag '%s' on asset '%s' doesn't match any defined custom tag",
                        customTagName,
                        assetId
                );

                requiredTagsNotSeen.remove(customTagName);

                var customTagDefinition = customTagDefinitions.get(customTagName);
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
}
