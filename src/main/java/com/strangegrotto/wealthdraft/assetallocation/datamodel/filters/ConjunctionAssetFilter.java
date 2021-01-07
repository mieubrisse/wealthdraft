package com.strangegrotto.wealthdraft.assetallocation.datamodel.filters;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.Sets;
import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import org.immutables.value.Value;

import java.util.List;
import java.util.Set;

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
    protected final Set<String> combineFilterMatches(Set<String> filterResultA, Set<String> filterResultB) {
        return Sets.intersection(filterResultA, filterResultB);
    }
}
