package com.strangegrotto.wealthdraft.backend.filters.impl;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.base.Preconditions;
import com.strangegrotto.wealthdraft.backend.assets.api.types.Asset;
import com.strangegrotto.wealthdraft.backend.filters.api.types.AssetFilter;
import com.strangegrotto.wealthdraft.backend.tags.custom.api.types.CustomTagDefinition;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
abstract class AbstractCompoundAssetFilter implements SerAssetFilter {
    @Override
    public final Map<String, Asset> apply(Map<String, AssetFilter> allFilters, Map<String, Asset> input) {
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
    public void validate(Map<String, ValidatableAssetFilter> allFilters, Map<String, CustomTagDefinition> customTags) {
        for (var filter : getConstituentFilters()) {
            filter.validate(allFilters, customTags);
        }
    }

    @Override
    public Optional<List<String>> checkForCycles(Map<String, ValidatableAssetFilter> allFilters, LinkedHashSet<String> parentFilters) {
        for (var filter : getConstituentFilters()) {
            var cycleOpt = filter.checkForCycles(allFilters, parentFilters);
            if (cycleOpt.isPresent()) {
                return cycleOpt;
            }
        }
        return Optional.empty();
    }

    protected abstract List<ValidatableAssetFilter> getConstituentFilters();

    // Reduce function used for deciding
    protected abstract Map<String, Asset> combineFilterMatches(Map<String, Asset> filterResultA, Map<String, Asset> filterResultB);
}
