package com.strangegrotto.wealthdraft.backend.filters.impl;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import com.strangegrotto.wealthdraft.backend.assets.api.types.Asset;
import com.strangegrotto.wealthdraft.backend.filters.api.types.AssetFilter;
import com.strangegrotto.wealthdraft.backend.tags.custom.api.types.CustomTagDefinition;
import com.strangegrotto.wealthdraft.backend.tags.intrinsic.IntrinsicAssetTag;
import org.immutables.value.Value;

import java.util.*;
import java.util.stream.Collectors;

@WealthdraftImmutableStyle
@Value.Immutable
// Deserialized using custom deserializer
abstract class TagAssetFilter implements SerAssetFilter {
    public abstract String getTag();

    public abstract String getValue();

    @Override
    public Map<String, Asset> apply(Map<String, AssetFilter> allFilters, Map<String, Asset> input) {
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
    public void validate(Map<String, ValidatableAssetFilter> allFilters, Map<String, CustomTagDefinition> customTags) {
        var intrinsicTagNamesAndValues = IntrinsicAssetTag.getTagNamesToAllowedValues();

        var tagName = getTag();
        Set<String> allowedValues;
        if (intrinsicTagNamesAndValues.containsKey(tagName)) {
            allowedValues = intrinsicTagNamesAndValues.get(tagName);
        } else if (customTags.containsKey(tagName)) {
            allowedValues = customTags.get(tagName).getAllowedValues();
        } else {
            throw new IllegalStateException(Strings.lenientFormat(
                    "Tag name filter on tag '%s' doesn't match any intrinsic or custom tag"
            ));
        }

        if (allowedValues.size() > 0) {
            var tagValue = getValue();
            Preconditions.checkState(
                    allowedValues.contains(tagValue),
                    "Found tag filter on tag '%s' and value '%s', but that value isn't allowed for that tag so it " +
                            "would always match nothing",
                    tagName,
                    tagValue
            );
        }
    }

    @Override
    public Optional<List<String>> checkForCycles(Map<String, ValidatableAssetFilter> allFilters, LinkedHashSet<String> parentFilters) {
        return Optional.empty();
    }
}
