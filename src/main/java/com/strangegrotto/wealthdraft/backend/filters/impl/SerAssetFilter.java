package com.strangegrotto.wealthdraft.backend.filters.impl;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.strangegrotto.wealthdraft.backend.filters.api.types.AssetFilter;

@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({
        @JsonSubTypes.Type(TagAssetFilter.class),
        @JsonSubTypes.Type(ConjunctionAssetFilter.class),
        @JsonSubTypes.Type(DisjunctionAssetFilter.class),
        @JsonSubTypes.Type(EmbeddedFilterAssetFilter.class)
})
interface SerAssetFilter extends AssetFilter { }
