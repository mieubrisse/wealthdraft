package com.strangegrotto.wealthdraft.assetimpls.stock;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import com.strangegrotto.wealthdraft.assets.temporal.AssetChange;
import com.strangegrotto.wealthdraft.assets.temporal.AssetParameterChange;
import org.immutables.value.Value;

import java.util.Optional;

@WealthdraftImmutableStyle
@Value.Immutable
@JsonDeserialize(as = ImmStockAssetChange.class)
public interface StockAssetChange extends AssetChange {
    @Value.Parameter
    @JsonProperty("quantity")
    Optional<AssetParameterChange> getQuantityChangeOpt();

    @Value.Parameter
    @JsonProperty("price")
    Optional<AssetParameterChange> getPriceChangeOpt();
}
