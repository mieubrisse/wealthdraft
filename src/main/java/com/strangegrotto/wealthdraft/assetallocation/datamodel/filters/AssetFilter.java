package com.strangegrotto.wealthdraft.assetallocation.datamodel.filters;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.strangegrotto.wealthdraft.assets.definition.Asset;

import java.util.Map;
import java.util.Set;

@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({
        @JsonSubTypes.Type(TagAssetFilter.class),
        @JsonSubTypes.Type(ConjunctionAssetFilter.class),
        @JsonSubTypes.Type(DisjunctionAssetFilter.class),
})
public interface AssetFilter {
    Map<String, Asset> apply(Map<String, Asset> input);
}
