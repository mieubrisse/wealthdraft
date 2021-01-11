package com.strangegrotto.wealthdraft.assetimpls.stock;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.annotations.VisibleForTesting;
import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import com.strangegrotto.wealthdraft.assethistory.impl.AbstractAssetSnapshot;
import com.strangegrotto.wealthdraft.assethistory.api.types.AssetSnapshot;
import com.strangegrotto.wealthdraft.errors.ValOrGerr;
import org.immutables.value.Value;

import java.math.BigDecimal;

@WealthdraftImmutableStyle
@Value.Immutable
@JsonDeserialize(as = ImmStockAssetSnapshot.class)
public abstract class StockAssetSnapshot extends AbstractAssetSnapshot<StockAssetChange> {
    public StockAssetSnapshot() {
        super(StockAssetChange.class);
    }

    // ================================================================================
    //               Logic custom this class, not filled by Immutables
    // ================================================================================
    @Override
    public final AssetSnapshot<StockAssetChange> projectOneMonth() {
        // TODO use price-pulling function
        return ImmStockAssetSnapshot.of(getQuantity(), getPrice());
    }

    @Override
    public final ValOrGerr<AssetSnapshot<StockAssetChange>> applyChangeInternal(StockAssetChange change) {
        var quantityChangeOpt = change.getQuantity();
        var newQuantity = getQuantity();
        if (quantityChangeOpt.isPresent()) {
            var newQuantityOrErr = quantityChangeOpt.get().apply(getQuantity());
            if (newQuantityOrErr.hasGerr()) {
                return ValOrGerr.propGerr(
                        newQuantityOrErr.getGerr(),
                        "An error occurred applying the change to the quantity"
                );
            }
            newQuantity = newQuantityOrErr.getVal();
        }

        var priceChangeOpt = change.getPrice();
        var newPrice = getPrice();
        if (priceChangeOpt.isPresent()) {
            var newPriceOrErr = priceChangeOpt.get().apply(getPrice());
            if (newPriceOrErr.hasGerr()) {
                return ValOrGerr.propGerr(
                        newPriceOrErr.getGerr(),
                        "An error occurred applying the change to the price"
                );
            }
            newPrice = newPriceOrErr.getVal();
        }

        return ValOrGerr.val(ImmStockAssetSnapshot.of(newQuantity, newPrice));
    }

    // ================================================================================
    //                     Functions filled by Immutables
    // ================================================================================
    @VisibleForTesting
    abstract BigDecimal getQuantity();

    @VisibleForTesting
    abstract BigDecimal getPrice();

    @Override
    @Value.Derived
    public BigDecimal getValue() {
        return getQuantity().multiply(getPrice());
    }

}
