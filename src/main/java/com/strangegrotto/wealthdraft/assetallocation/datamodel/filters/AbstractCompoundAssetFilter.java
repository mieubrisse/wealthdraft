package com.strangegrotto.wealthdraft.assetallocation.datamodel.filters;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.base.Preconditions;
import com.strangegrotto.wealthdraft.assets.definition.Asset;

import java.util.List;
import java.util.Map;
import java.util.Set;

@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
public abstract class AbstractCompoundAssetFilter implements AssetFilter {
    @Override
    public final Map<String, Asset> apply(Map<String, Asset> input) {
        var constituentFilters = getConstituentFilters();
        Preconditions.checkState(constituentFilters.size() > 0, "Compound filter requires >= 1 constituent filter");

        var filterResultsOpt = getConstituentFilters().stream()
                .map(filter -> filter.apply(input))
                .reduce(this::combineFilterMatches);

        // We're okay to ignore the Optional.isPresent() check because we assert that the constituent filters
        //  list length is > 0 at the start of the function
        //noinspection OptionalGetWithoutIsPresent
        return filterResultsOpt.get();
    }

    protected abstract List<AssetFilter> getConstituentFilters();

    // Reduce function used for deciding
    protected abstract Map<String, Asset> combineFilterMatches(Map<String, Asset> filterResultA, Map<String, Asset> filterResultB);
}
