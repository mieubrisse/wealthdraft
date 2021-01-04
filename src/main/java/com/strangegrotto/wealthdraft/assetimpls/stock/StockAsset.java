package com.strangegrotto.wealthdraft.assetimpls.stock;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import com.strangegrotto.wealthdraft.assetimpls.AssetTypeTagValue;
import com.strangegrotto.wealthdraft.assets.definition.Asset;
import com.strangegrotto.wealthdraft.assets.temporal.AssetChange;
import com.strangegrotto.wealthdraft.assets.temporal.AssetSnapshot;
import org.immutables.value.Value;

import java.util.Map;

@WealthdraftImmutableStyle
@Value.Immutable
@JsonDeserialize(as = ImmStockAsset.class)
public abstract class StockAsset implements Asset<StockAssetSnapshot, StockAssetChange> {
    // ================================================================================
    //               Logic custom this class, not filled by Immutables
    // ================================================================================
    @Override
    public final AssetTypeTagValue getAssetTypeTagValue() {
        return AssetTypeTagValue.STOCK;
    }

    @Override
    public final Class<StockAssetChange> getChangeType() {
        return StockAssetChange.class;
    }

    @Override
    public final Class<StockAssetSnapshot> getSnapshotType() {
        return StockAssetSnapshot.class;
    }

    // ================================================================================
    //                     Functions filled by Immutables
    // ================================================================================

    @Override
    public abstract String getName();

    @Override
    public abstract Map<String, String> getTags();
}
