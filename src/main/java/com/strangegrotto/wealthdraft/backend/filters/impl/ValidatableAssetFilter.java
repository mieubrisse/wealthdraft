package com.strangegrotto.wealthdraft.backend.filters.impl;

import com.strangegrotto.wealthdraft.backend.filters.api.types.AssetFilter;
import com.strangegrotto.wealthdraft.backend.tags.custom.api.types.CustomTagDefinition;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

interface ValidatableAssetFilter extends AssetFilter{
    /**
     * Validates the given filter against the provided objects
     */
    void validate(Map<String, ValidatableAssetFilter> allFilters, Map<String, CustomTagDefinition> customTags);

    /**
     * Checks for cycles in the filter and any child filters
     *
     * @param allFilters
     * @param parentFilters The names of the parent filters that have been seen so far
     * @return The cycle, if one exists
     */
    // NOTE: We need to pass the allFilters argument so that the EmbeddedFilterAssetFilter class can work,
    //  but I'm not super happy with needing to do this. Ideally, rather than needing to pass this as an argument
    //  to *every* call, it would be instance-specific data given only to the embedded filter class
    //  at time of instantion.. but this would require writing custom deserializers for the TargetAssetAllocations
    //  and AssetFilter classes
    Optional<List<String>> checkForCycles(Map<String, ValidatableAssetFilter> allFilters, LinkedHashSet<String> parentFilters);
}