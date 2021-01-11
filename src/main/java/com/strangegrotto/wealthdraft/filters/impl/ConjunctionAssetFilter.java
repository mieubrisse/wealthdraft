package com.strangegrotto.wealthdraft.filters.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.Sets;
import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import com.strangegrotto.wealthdraft.assets.impl.SerAsset;
import com.strangegrotto.wealthdraft.filters.ImmConjunctionAssetFilter;
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
    public abstract List<SerAssetFilter> getFilters();

    @Override
    protected final List<SerAssetFilter> getConstituentFilters() {
        return getFilters();
    }

    @Override
    protected Map<String, SerAsset> combineFilterMatches(Map<String, SerAsset> filterResultA, Map<String, SerAsset> filterResultB) {
        var matchingAssetIds = Sets.intersection(filterResultA.keySet(), filterResultB.keySet());
        return filterResultA.entrySet().stream()
                .filter(entry -> matchingAssetIds.contains(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
