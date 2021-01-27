package com.strangegrotto.wealthdraft.assetimpls.stock;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import com.strangegrotto.wealthdraft.backend.projections.api.types.AssetChange;
import com.strangegrotto.wealthdraft.backend.projections.impl.temporal.SerAssetParameterChange;
import org.immutables.value.Value;

import java.util.Optional;

@WealthdraftImmutableStyle
@Value.Immutable
@JsonDeserialize(as = ImmStockAssetChange.class)
public interface StockAssetChange extends AssetChange {
    @Value.Parameter
    @JsonProperty("quantity")
    Optional<SerAssetParameterChange> getQuantity();

    @Value.Parameter
    @JsonProperty("price")
    Optional<SerAssetParameterChange> getPrice();
}
