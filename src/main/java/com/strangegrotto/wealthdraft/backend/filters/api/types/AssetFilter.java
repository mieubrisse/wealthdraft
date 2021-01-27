package com.strangegrotto.wealthdraft.backend.filters.api.types;

import com.strangegrotto.wealthdraft.backend.assets.api.types.Asset;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AssetFilter {
    // NOTE: We need to pass the allFilters argument so that the EmbeddedFilterAssetFilter class can work,
    //  but I'm not super happy with needing to do this. Ideally, rather than needing to pass this as an argument
    //  to *every* call, it would be instance-specific data given only to the embedded filter class
    //  at time of instantion.. but this would require writing custom deserializers for the TargetAssetAllocations
    //  and AssetFilter classes
    Map<String, Asset> apply(Map<String, AssetFilter> allFilters, Map<String, Asset> input);
}
