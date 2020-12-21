package com.strangegrotto.wealthdraft.assetimpls.stock;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.strangegrotto.wealthdraft.assets.temporal.AssetParameterChange;

import java.util.Optional;

public interface StockAssetChange {
    @JsonProperty("quantity")
    Optional<AssetParameterChange> getQuantityChangeOpt();

    @JsonProperty("price")
    Optional<AssetParameterChange> getPriceChangeOpt();
}
