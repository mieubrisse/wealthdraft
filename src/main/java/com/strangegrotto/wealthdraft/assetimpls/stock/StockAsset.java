package com.strangegrotto.wealthdraft.assetimpls.stock;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.strangegrotto.wealthdraft.assetimpls.AssetTypeTagValue;
import com.strangegrotto.wealthdraft.assetimpls.bankaccount.BankAccountAssetChange;
import com.strangegrotto.wealthdraft.assetimpls.bankaccount.BankAccountAssetSnapshot;
import com.strangegrotto.wealthdraft.assets.definition.AbstractAsset;
import com.strangegrotto.wealthdraft.assets.definition.Asset;
import com.strangegrotto.wealthdraft.assets.temporal.AssetChange;
import com.strangegrotto.wealthdraft.assets.temporal.AssetSnapshot;
import org.immutables.value.Value;

import java.util.HashMap;
import java.util.Map;

public final class StockAsset extends AbstractAsset {
    @JsonCreator
    public StockAsset(
            @JsonProperty("name") String name,
            @JsonProperty("tags") @JsonSetter(nulls = Nulls.AS_EMPTY) Map<String, String> customTags
    ) {
        super(name, customTags);
    }

    @Override
    public Class<? extends AssetChange> getChangeType() {
        return null;
    }

    @Override
    public Class<? extends AssetSnapshot> getSnapshotType() {
        return StockAssetSnapshot.class;
    }

    @Override
    public AssetTypeTagValue getAssetTypeTagValue() {
        return null;
    }
}
