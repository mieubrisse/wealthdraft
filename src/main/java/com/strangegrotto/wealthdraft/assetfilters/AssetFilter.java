package com.strangegrotto.wealthdraft.assetfilters;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.strangegrotto.wealthdraft.assets.impl.SerAsset;

import java.util.*;

@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({
        @JsonSubTypes.Type(TagAssetFilter.class),
        @JsonSubTypes.Type(ConjunctionAssetFilter.class),
        @JsonSubTypes.Type(DisjunctionAssetFilter.class),
        @JsonSubTypes.Type(EmbeddedFilterAssetFilter.class)
})
public interface AssetFilter {
    // NOTE: We need to pass the allFilters argument so that the EmbeddedFilterAssetFilter class can work,
    //  but I'm not super happy with needing to do this. Ideally, rather than needing to pass this as an argument
    //  to *every* call, it would be instance-specific data given only to the embedded filter class
    //  at time of instantion.. but this would require writing custom deserializers for the TargetAssetAllocations
    //  and AssetFilter classes
    Map<String, SerAsset> apply(Map<String, AssetFilter> allFilters, Map<String, SerAsset> input);

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
    Optional<List<String>> checkForCycles(Map<String, AssetFilter> allFilters, LinkedHashSet<String> parentFilters);
}
