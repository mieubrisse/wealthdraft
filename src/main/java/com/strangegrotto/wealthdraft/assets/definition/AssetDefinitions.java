package com.strangegrotto.wealthdraft.assets.definition;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import org.immutables.value.Value;

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

        for (var customTagName : customTagDefinitions.keySet()) {
            Preconditions.checkState(
                    !IntrinsicAssetTag.getAllNames().contains(customTagName),
                    "Custom tag '%s' collides with an intrinsic tag",
                    customTagName
            );
        }

        var assets = getAssets();
        for (var assetId : assets.keySet()) {
            var asset = assets.get(assetId);
            var customTagsForAsset = asset.getTags();
            for (var customTag : customTagsForAsset.keySet()) {
                var customTagValue = customTagsForAsset.get(customTag);

                Preconditions.checkState(
                        customTagDefinitions.containsKey(customTag),
                        "Custom tag '%s' on asset '%s' doesn't match any defined custom tag",
                        customTag,
                        assetId
                );

                var customTagDefinition = customTagDefinitions.get(customTag);
                var allowedTagValues = customTagDefinition.getAllowedValues();
                if (allowedTagValues.size() > 0) {
                    Preconditions.checkState(
                            allowedTagValues.contains(customTagValue),
                            "Tag '%s' for asset '%s' has a value not in the allowed values list for the tag",
                            customTag,
                            customTagValue
                    );
                }
            }
        }
    }
}
