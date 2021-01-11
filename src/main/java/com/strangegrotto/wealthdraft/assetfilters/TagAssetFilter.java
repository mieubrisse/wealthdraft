package com.strangegrotto.wealthdraft.assetfilters;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import com.strangegrotto.wealthdraft.assets.api.types.AssetType;
import com.strangegrotto.wealthdraft.assets.impl.SerAsset;
import com.strangegrotto.wealthdraft.tagstores.custom.api.types.CustomTagDefinition;
import com.strangegrotto.wealthdraft.tagstores.intrinsic.IntrinsicAssetTag;
import org.immutables.value.Value;

import java.util.*;
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
    public Map<String, SerAsset> apply(Map<String, AssetFilter> allFilters, Map<String, SerAsset> input) {
        var tagName = getTag();
        var tagValue = getValue();
        return input.entrySet().stream()
                .filter(entry -> {
                    var asset = entry.getValue();
                    var assetTags = asset.getTags();
                    return assetTags.containsKey(tagName) && tagValue.equals(assetTags.get(tagName));
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Optional<List<String>> checkForCycles(Map<String, AssetFilter> allFilters, LinkedHashSet<String> parentFilters) {
        return Optional.empty();
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
                    AssetType.valueOf(tagValue);
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
