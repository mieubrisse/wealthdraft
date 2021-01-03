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
public abstract class BankAccountAsset implements Asset {
    // ================================================================================
    //               Logic custom this class, not filled by Immutables
    // ================================================================================
    @Override
    public final AssetTypeTagValue getAssetTypeTagValue() {
        return AssetTypeTagValue.BANK_ACCOUNT;
    }

    @Override
    public final Class<? extends AssetChange> getChangeType() {
        return BankAccountAssetChange.class;
    }

    @Override
    public final Class<? extends AssetSnapshot> getSnapshotType() {
        return BankAccountAssetSnapshot.class;
    }

    // ================================================================================
    //                     Functions filled by Immutables
    // ================================================================================
    @Override
    public abstract String getName();

    @Override
    public abstract Map<String, String> getTags();
}
