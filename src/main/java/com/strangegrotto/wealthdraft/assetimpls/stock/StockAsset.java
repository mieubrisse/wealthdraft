package com.strangegrotto.wealthdraft.assetimpls.stock;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
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
            @JsonProperty("tags") Map<String, String> tags
    ) {
        super(name, tags);
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
