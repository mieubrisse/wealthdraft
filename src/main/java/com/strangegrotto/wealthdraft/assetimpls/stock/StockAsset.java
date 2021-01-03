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
public abstract class StockAsset implements Asset {
    // ================================================================================
    //               Logic custom this class, not filled by Immutables
    // ================================================================================
    @Override
    public final Class<? extends AssetSnapshot> getSnapshotType() {
        return StockAssetSnapshot.class;
    }

    @Override
    public abstract String getName();

    @Override
    public abstract Map<String, String> getTags();

    // ================================================================================
    //                     Functions filled by Immutables
    // ================================================================================
    @Override
    public final AssetTypeTagValue getAssetTypeTagValue() {
        return AssetTypeTagValue.STOCK;
    }

    @Override
    public final Class<? extends AssetChange> getChangeType() {
        return StockAssetChange.class;
    }
}
