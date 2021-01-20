package com.strangegrotto.wealthdraft.backend.filters.impl;

import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import com.strangegrotto.wealthdraft.backend.assets.api.types.Asset;
import com.strangegrotto.wealthdraft.backend.filters.api.types.AssetFilter;
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
    public Optional<List<String>> checkForCycles(Map<String, AssetFilter> allFilters, LinkedHashSet<String> parentFilters) {
        return Optional.empty();
    }
}
