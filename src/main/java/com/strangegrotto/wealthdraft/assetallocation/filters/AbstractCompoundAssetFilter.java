package com.strangegrotto.wealthdraft.assetallocation.filters;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import com.strangegrotto.wealthdraft.assets.definition.Asset;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
public abstract class AbstractCompoundAssetFilter implements AssetFilter {
    @Override
    public final Set<String> apply(Map<String, Asset<?, ?>> allAssets, Set<String> currentSelection) {
        var constituentFilters = getConstituentFilters();
        Preconditions.checkState(constituentFilters.size() > 0, "Compound filter requires >= 1 constituent filter");

        var filterResultsOpt = getConstituentFilters().stream()
                .map(filter -> filter.apply(allAssets, currentSelection))
                .reduce(this::combineFilterMatches);

        // We're okay to ignore the Optional.isPresent() check because we assert that the constituent filters
        //  list length is > 0 at the start of the function
        //noinspection OptionalGetWithoutIsPresent
        return filterResultsOpt.get();
    }

    protected abstract List<AssetFilter> getConstituentFilters();

    // Reduce function used for deciding
    protected abstract Set<String> combineFilterMatches(Set<String> filterResultA, Set<String> filterResultB);
}
