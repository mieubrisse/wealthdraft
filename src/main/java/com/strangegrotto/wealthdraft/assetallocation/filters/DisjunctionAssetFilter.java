package com.strangegrotto.wealthdraft.assetallocation.filters;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.Sets;
import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import org.immutables.value.Value;

import java.util.List;
import java.util.Set;

@WealthdraftImmutableStyle
@Value.Immutable
@JsonDeserialize(as = ImmDisjunctionAssetFilter.class)
public abstract class DisjunctionAssetFilter extends AbstractCompoundAssetFilter {
    @Value.Parameter
    @JsonProperty("any")
    public abstract List<AssetFilter> getTags();

    @Override
    protected final List<AssetFilter> getConstituentFilters() {
        return this.getTags();
    }

    @Override
    protected final Set<String> combineFilterMatches(Set<String> filterResultA, Set<String> filterResultB) {
        return Sets.union(filterResultA, filterResultB);
    }
}
