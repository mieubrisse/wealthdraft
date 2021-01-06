package com.strangegrotto.wealthdraft.assetallocation.filters;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.google.common.collect.Sets;
import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import org.immutables.value.Value;

import java.util.List;
import java.util.Set;

@WealthdraftImmutableStyle
@Value.Immutable
@JsonDeserialize(as = ImmDisjunctiveAssetFilter.class)
public abstract class DisjunctiveAssetFilter extends AbstractCompoundAssetFilter {
    @Value.Parameter
    @JsonProperty("any")
    public abstract List<AssetFilter> getTags();

    @Override
    protected List<AssetFilter> getConstituentFilters() {
        return this.getTags();
    }

    @Override
    protected Set<String> combineFilterMatches(Set<String> filterResultA, Set<String> filterResultB) {
        return Sets.union(filterResultA, filterResultB);
    }
}
