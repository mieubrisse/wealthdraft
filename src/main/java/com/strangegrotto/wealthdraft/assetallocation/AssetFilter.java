package com.strangegrotto.wealthdraft.assetallocation;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.strangegrotto.wealthdraft.assets.definition.Asset;

import java.util.Map;

@JsonDeserialize(as = ImmConjunctiveAssetTagFilter.class)
public interface AssetFilter {
    Map<String, Asset<?, ?>> apply(Map<String, Asset<?, ?>> input);

    String getStringRepresentation();
}
