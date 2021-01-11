package com.strangegrotto.wealthdraft.filters.impl;

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
public abstract class TagAssetFilter implements SerAssetFilter {
    public abstract String getTag();

    public abstract String getValue();

    @Override
    public Map<String, SerAsset> apply(Map<String, SerAssetFilter> allFilters, Map<String, SerAsset> input) {
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
    public Optional<List<String>> checkForCycles(Map<String, SerAssetFilter> allFilters, LinkedHashSet<String> parentFilters) {
        return Optional.empty();
    }
}
