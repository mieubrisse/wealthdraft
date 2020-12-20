package com.strangegrotto.wealthdraft.networth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import com.strangegrotto.wealthdraft.networth.assets.AbstractAsset;
import com.strangegrotto.wealthdraft.networth.assets.AssetSnapshot;
import com.strangegrotto.wealthdraft.networth.assets.DefaultAssetTag;
import com.strangegrotto.wealthdraft.networth.projections.AssetChange;
import org.immutables.value.Value;

import java.util.Map;

@WealthdraftImmutableStyle
@Value.Immutable
@JsonDeserialize(as = ImmBankAccountAsset.class)
public abstract class BankAccountAsset extends AbstractAsset {
    // TODO need to make sure there aren't collisions across asset types
    private static final String assetType = "Bank Account";

    @Override
    @JsonIgnore
    public Class<? extends AssetChange> getChangeType() {
        return BankAccountAssetChange.class;
    }

    @Override
    @JsonIgnore
    public Class<? extends AssetSnapshot> getSnapshotType() {
        return BankAccountAssetSnapshot.class;
    }

    @Override
    @JsonIgnore
    protected Map<DefaultAssetTag, String> getDefaultTags() {
        return Map.of(
                DefaultAssetTag.ASSET_TYPE, assetType
        );
    }
}
