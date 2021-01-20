package com.strangegrotto.wealthdraft.backend.filters.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.Sets;
import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import com.strangegrotto.wealthdraft.backend.assets.api.types.Asset;
import com.strangegrotto.wealthdraft.backend.filters.ImmDisjunctionAssetFilter;
import com.strangegrotto.wealthdraft.backend.filters.api.types.AssetFilter;
import org.immutables.value.Value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@WealthdraftImmutableStyle
@Value.Immutable
@JsonDeserialize(as = ImmDisjunctionAssetFilter.class)
abstract class DisjunctionAssetFilter extends AbstractCompoundAssetFilter {
    @Value.Parameter
    @JsonProperty("any")
    public abstract List<AssetFilter> getTags();

    @Override
    protected final List<AssetFilter> getConstituentFilters() {
        return this.getTags();
    }

    @Override
    protected Map<String, Asset> combineFilterMatches(Map<String, Asset> filterResultA, Map<String, Asset> filterResultB) {
        var matchingIds = Sets.union(filterResultA.keySet(), filterResultB.keySet());
        var matchingAEntries = filterResultA.entrySet().stream()
                .filter(entry -> matchingIds.contains(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        var matchingBEntries = filterResultB.entrySet().stream()
                .filter(entry -> matchingIds.contains(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // We put A entries second so that if A and B contain the same ID, the A version is the one that
        //  ends up in the final map. It shouldn't make a difference because A and B should have the exact same
        //  asset for the same assetId, but just in case
        var result = new HashMap<String, Asset>();
        result.putAll(matchingBEntries);
        result.putAll(matchingAEntries);

        return Map.copyOf(result);
    }
}
