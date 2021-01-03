package com.strangegrotto.wealthdraft.assetimpls.bankaccount;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import com.strangegrotto.wealthdraft.assetimpls.AssetTypeTagValue;
import com.strangegrotto.wealthdraft.assets.definition.AbstractAsset;
import com.strangegrotto.wealthdraft.assets.definition.Asset;
import com.strangegrotto.wealthdraft.assets.temporal.AssetSnapshot;
import com.strangegrotto.wealthdraft.assets.definition.IntrinsicAssetTag;
import com.strangegrotto.wealthdraft.assets.temporal.AssetChange;
import org.immutables.value.Value;

import java.util.HashMap;
import java.util.Map;

public final class BankAccountAsset extends AbstractAsset {
    @JsonCreator
    public BankAccountAsset(
            @JsonProperty("name") String name,
            @JsonProperty("tags") @JsonSetter(nulls = Nulls.AS_EMPTY) Map<String, String> customTags
    ) {
        super(name, customTags);
    }

    @Override
    public Class<? extends AssetChange> getChangeType() {
        return BankAccountAssetChange.class;
    }

    @Override
    public Class<? extends AssetSnapshot> getSnapshotType() {
        return BankAccountAssetSnapshot.class;
    }

    @Override
    public AssetTypeTagValue getAssetTypeTagValue() {
        return AssetTypeTagValue.BANK_ACCOUNT;
    }
}
