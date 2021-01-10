package com.strangegrotto.wealthdraft.assetallocation.datamodel.filters;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import com.strangegrotto.wealthdraft.assetimpls.AssetTypeTagValue;
import com.strangegrotto.wealthdraft.assets.definition.Asset;
import com.strangegrotto.wealthdraft.assets.definition.CustomTagDefinition;
import com.strangegrotto.wealthdraft.assets.definition.IntrinsicAssetTag;
import org.immutables.value.Value;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@WealthdraftImmutableStyle
@Value.Immutable
// Deserialized using custom deserializer
public abstract class TagAssetFilter implements AssetFilter {
    // Protected because this is only used in the 'check' method
    @Value.Parameter
    protected abstract Map<String, CustomTagDefinition> getCustomTags();

    public abstract String getTag();

    public abstract String getValue();

    @Override
    public final Set<String> apply(Map<String, Asset<?, ?>> allAssets, Set<String> currentSelection) {
        var tagName = getTag();
        var tagValue = getValue();
        return allAssets.entrySet().stream()
                .filter(entry -> {
                    var asset = entry.getValue();
                    var assetTags = asset.getTags();
                    return assetTags.containsKey(tagName) && tagValue.equals(assetTags.get(tagName));
                })
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    @Value.Check
    public void check() {
        var customTags = getCustomTags();

        Set<String> intrinsicTagNames = IntrinsicAssetTag.getAllTagNames();
        Set<String> customTagNames = customTags.keySet();

        Set<String> allTagNames = new HashSet<>();
        allTagNames.addAll(intrinsicTagNames);
        allTagNames.addAll(customTagNames);

        var tagName = getTag();
        Preconditions.checkState(
                allTagNames.contains(tagName),
                "Tag '%s' is neither an instrinsic nor custom tag",
                tagName
        );

        var tagValue = getValue();
        if (intrinsicTagNames.contains(tagName)) {
            if (tagName.equals(IntrinsicAssetTag.ASSET_TYPE.getTagName())) {
                // TODO If we wanted, we could do a better job of handling this - rather than
                //  having special cases for each intrinsic asset tag, just make a TagDefinition
                //  object with allowed values that both CustomAssetTagDefinition and IntrinsicAssetTagDefinition
                //  implement
                try {
                    AssetTypeTagValue.valueOf(tagValue);
                } catch (IllegalArgumentException e) {
                    throw new IllegalStateException(Strings.lenientFormat(
                            "Asset type tag has unrecognized value '%s'",
                            tagValue
                    ));
                }
            } else {
                throw new IllegalStateException(Strings.lenientFormat(
                        "Unrecognized intrinsic asset tag '%s'; this is a code bug",
                        tagName
                ));
            }
        } else if (customTagNames.contains(tagName)) {
            var customDefinition = customTags.get(tagName);
            var allowedValues = customDefinition.getAllowedValues();
            if (allowedValues.size() > 0) {
                Preconditions.checkState(
                        allowedValues.contains(tagValue),
                        "Tag '%s' only allows certain values and '%s' isn't one of them",
                        tagValue
                );
            }
        }
    }
}
