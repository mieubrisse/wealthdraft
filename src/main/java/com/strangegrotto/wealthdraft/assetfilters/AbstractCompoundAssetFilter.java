package com.strangegrotto.wealthdraft.assetfilters;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.base.Preconditions;
import com.strangegrotto.wealthdraft.assets.impl.SerAsset;

import java.util.*;

@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
public abstract class AbstractCompoundAssetFilter implements AssetFilter {
    @Override
    public final Map<String, SerAsset> apply(Map<String, AssetFilter> allFilters, Map<String, SerAsset> input) {
        var constituentFilters = getConstituentFilters();
        Preconditions.checkState(constituentFilters.size() > 0, "Compound filter requires >= 1 constituent filter");

        var filterResultsOpt = getConstituentFilters().stream()
                .map(filter -> filter.apply(allFilters, input))
                .reduce(this::combineFilterMatches);

        // We're okay to ignore the Optional.isPresent() check because we assert that the constituent filters
        //  list length is > 0 at the start of the function
        //noinspection OptionalGetWithoutIsPresent
        return filterResultsOpt.get();
    }

    @Override
    public Optional<List<String>> checkForCycles(Map<String, AssetFilter> allFilters, LinkedHashSet<String> parentFilters) {
        for (var filter : getConstituentFilters()) {
            var cycleOpt = filter.checkForCycles(allFilters, parentFilters);
            if (cycleOpt.isPresent()) {
                return cycleOpt;
            }
        }
        return Optional.empty();
    }

    protected abstract List<AssetFilter> getConstituentFilters();

    // Reduce function used for deciding
    protected abstract Map<String, SerAsset> combineFilterMatches(Map<String, SerAsset> filterResultA, Map<String, SerAsset> filterResultB);
}
