package com.strangegrotto.wealthdraft.filters.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.Sets;
import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import com.strangegrotto.wealthdraft.assets.api.types.Asset;
import com.strangegrotto.wealthdraft.assets.impl.SerAsset;
import com.strangegrotto.wealthdraft.filters.ImmConjunctionAssetFilter;
import com.strangegrotto.wealthdraft.filters.api.types.AssetFilter;
import org.immutables.value.Value;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@WealthdraftImmutableStyle
@Value.Immutable
@JsonDeserialize(as = ImmConjunctionAssetFilter.class)
public abstract class ConjunctionAssetFilter extends AbstractCompoundAssetFilter {
    @Value.Parameter
    @JsonProperty("all")
    public abstract List<AssetFilter> getFilters();

    @Override
    protected final List<AssetFilter> getConstituentFilters() {
        return getFilters();
    }

    @Override
    protected Map<String, Asset> combineFilterMatches(Map<String, Asset> filterResultA, Map<String, Asset> filterResultB) {
        var matchingAssetIds = Sets.intersection(filterResultA.keySet(), filterResultB.keySet());
        return filterResultA.entrySet().stream()
                .filter(entry -> matchingAssetIds.contains(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
