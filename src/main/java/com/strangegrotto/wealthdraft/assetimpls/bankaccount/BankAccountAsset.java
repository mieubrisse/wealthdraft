package com.strangegrotto.wealthdraft.assetimpls.bankaccount;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import com.strangegrotto.wealthdraft.assetimpls.AssetTypeTagValue;
import com.strangegrotto.wealthdraft.assets.definition.Asset;
import com.strangegrotto.wealthdraft.assets.temporal.AssetSnapshot;
import com.strangegrotto.wealthdraft.assets.definition.IntrinsicAssetTag;
import com.strangegrotto.wealthdraft.assets.temporal.AssetChange;
import org.immutables.value.Value;

import java.util.HashMap;
import java.util.Map;

@WealthdraftImmutableStyle
@Value.Immutable
@JsonDeserialize(as = ImmBankAccountAsset.class)
public interface BankAccountAsset extends Asset {
    // TODO need to make sure there aren't collisions across asset types
    String assetType = "Bank Account";

    @Override
    @Value.Derived
    default Class<? extends AssetChange> getChangeType() {
        return BankAccountAssetChange.class;
    }

    @Override
    @Value.Derived
    default Class<? extends AssetSnapshot> getSnapshotType() {
        return BankAccountAssetSnapshot.class;
    }

    @Override
    @Value.Derived
    default AssetTypeTagValue getAssetTypeTagValue() {
        return AssetTypeTagValue.BANK_ACCOUNT;
    }

    @Override
    @Value.Derived
    default Map<String, String> getAssetSpecificTags() {
        return new HashMap<>();
    }

    @Override
    @JsonProperty("tags")
    Map<String, String> getCustomTags();
}
