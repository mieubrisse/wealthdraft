package com.strangegrotto.wealthdraft.assetimpls.stock;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.strangegrotto.wealthdraft.assets.temporal.AssetChange;
import com.strangegrotto.wealthdraft.assets.temporal.AssetParameterChange;
import com.strangegrotto.wealthdraft.assets.temporal.AssetSnapshot;
import com.strangegrotto.wealthdraft.errors.ValOrGerr;

import java.math.BigDecimal;
import java.util.Optional;

public class StockAssetSnapshot implements AssetSnapshot {
    private final BigDecimal quantity;
    private final BigDecimal price;

    @JsonCreator
    public StockAssetSnapshot(
            @JsonProperty("quantity") BigDecimal quantity,
            @JsonProperty("price") BigDecimal price
    ) {
        this.quantity = quantity;
        this.price = price;
    }

    @Override
    public BigDecimal getValue() {
        return this.quantity.multiply(this.price);
    }

    @Override
    public AssetSnapshot projectOneMonth() {
        // TODO use price-pulling function
        return new StockAssetSnapshot(this.quantity, this.price);
    }

    @Override
    public ValOrGerr<AssetSnapshot> applyChange(AssetChange change) {
        // TODO Get rid of this nasty casty
        StockAssetChange castedChange = (StockAssetChange) change;

        var quantityChangeOpt = castedChange.getQuantityChangeOpt();
        var newQuantity = this.quantity;
        if (quantityChangeOpt.isPresent()) {
            var newQuantityOrErr = quantityChangeOpt.get().apply(this.quantity);
            if (newQuantityOrErr.hasGerr()) {
                return ValOrGerr.propGerr(
                        newQuantityOrErr.getGerr(),
                        "An error occurred applying the change to the quantity"
                );
            }
            newQuantity = newQuantityOrErr.getVal();
        }

        var priceChangeOpt = castedChange.getPriceChangeOpt();
        var newPrice = this.price;
        if (priceChangeOpt.isPresent()) {
            var newPriceOrErr = priceChangeOpt.get().apply(this.price);
            if (newPriceOrErr.hasGerr()) {
                return ValOrGerr.propGerr(
                        newPriceOrErr.getGerr(),
                        "An error occurred applying the change to the price"
                );
            }
            newPrice = newPriceOrErr.getVal();
        }

        return ValOrGerr.val(new StockAssetSnapshot(newQuantity, newPrice));
    }
}
