package com.strangegrotto.wealthdraft.assetimpls.bankaccount;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import com.strangegrotto.wealthdraft.assetimpls.AssetTypeTagValue;
import com.strangegrotto.wealthdraft.assets.definition.Asset;
import com.strangegrotto.wealthdraft.assets.temporal.AssetSnapshot;
import com.strangegrotto.wealthdraft.assets.temporal.AssetChange;
import org.immutables.value.Value;

import java.util.Map;

@WealthdraftImmutableStyle
@Value.Immutable
@JsonDeserialize(as = ImmBankAccountAsset.class)
public abstract class BankAccountAsset extends Asset<BankAccountAssetSnapshot, BankAccountAssetChange> {
    // ================================================================================
    //               Logic custom this class, not filled by Immutables
    // ================================================================================
    @Override
    public final AssetTypeTagValue getAssetTypeTagValue() {
        return AssetTypeTagValue.BANK_ACCOUNT;
    }

    @Override
    public final Class<BankAccountAssetChange> getChangeType() {
        return BankAccountAssetChange.class;
    }

    @Override
    public final Class<BankAccountAssetSnapshot> getSnapshotType() {
        return BankAccountAssetSnapshot.class;
    }

    // ================================================================================
    //                     Functions filled by Immutables
    // ================================================================================
    @Override
    public abstract String getName();

    // TODO Rather than needing to do this here, push this to Asset by killing this class entirely and replacing with an enum
    @JsonProperty("tags")
    @Override
    public abstract Map<String, String> getCustomTags();
}
