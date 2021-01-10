package com.strangegrotto.wealthdraft.assetallocation.datamodel.filters;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.strangegrotto.wealthdraft.assets.definition.Asset;

import java.util.*;

@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({
        @JsonSubTypes.Type(TagAssetFilter.class),
        @JsonSubTypes.Type(ConjunctionAssetFilter.class),
        @JsonSubTypes.Type(DisjunctionAssetFilter.class),
        @JsonSubTypes.Type(EmbeddedFilterAssetFilter.class)
})
public interface AssetFilter {
    Map<String, Asset> apply(Map<String, AssetFilter> allFilters, Map<String, Asset> input);

    /**
     * Checks for cycles in the filter and any child filters
     *
     * @param allFilters
     * @param parentFilters The names of the parent filters that have been seen so far
     * @return The cycle, if one exists
     */
    Optional<List<String>> checkForCycles(Map<String, AssetFilter> allFilters, LinkedHashSet<String> parentFilters);
}
