package com.strangegrotto.wealthdraft.networth;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import com.strangegrotto.wealthdraft.networth.assets.Asset;
import com.strangegrotto.wealthdraft.networth.assets.AssetTag;
import com.strangegrotto.wealthdraft.networth.assets.DefaultAssetTag;
import com.strangegrotto.wealthdraft.networth.assets.AssetType;
import org.immutables.value.Value;

import java.util.Map;

@WealthdraftImmutableStyle
@Value.Immutable
@JsonDeserialize(as = ImmBankAccountAsset.class)
public abstract class BankAccountAsset extends Asset {
    // TODO need to make sure there aren't collisions across asset types
    private static final String assetType = "Bank Account";

    @Value.Derived
    public AssetType getType() {
        return AssetType.BANK_ACCOUNT;
    }

    @Override
    protected final Map<DefaultAssetTag, String> getDefaultTags() {
        return Map.of(
                DefaultAssetTag.ASSET_TYPE, assetType
        );
    }
}
