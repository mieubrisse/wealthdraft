package com.strangegrotto.wealthdraft.networth.assets;

import com.strangegrotto.wealthdraft.networth.BankAccountAssetChange;
import com.strangegrotto.wealthdraft.networth.BankAccountAssetSnapshot;
import com.strangegrotto.wealthdraft.networth.BankAccountAssetSnapshotTransformer;
import com.strangegrotto.wealthdraft.networth.projections.AssetChange;
import com.strangegrotto.wealthdraft.networth.projections.AssetSnapshotTransformer;

public enum AssetType {
    BANK_ACCOUNT(
            BankAccountAssetSnapshot.class,
            BankAccountAssetChange.class,
            BankAccountAssetSnapshotTransformer.INSTANCE
    );

    private final Class<? extends AssetSnapshot> snapshotType;
    private final Class<? extends AssetChange> changeType;
    private final AssetSnapshotTransformer<?, ?> assetSnapshotTransformer;

    <S extends AssetSnapshot, C extends AssetChange<S>, T extends AssetSnapshotTransformer<S, C>> AssetType(
            Class<S> snapshotType,
            Class<C> changeType,
            T assetSnapshotTransformer) {
        this.snapshotType = snapshotType;
        this.changeType = changeType;
        this.assetSnapshotTransformer = assetSnapshotTransformer;
    }

    public Class<? extends AssetSnapshot> getSnapshotType() {
        return this.snapshotType;
    }

    public Class<? extends AssetChange> getChangeType() {
        return changeType;
    }

    public <S extends AssetSnapshot> AssetSnapshotTransformer<S, AssetChange<S>> getAssetSnapshotTransformer() {
        return this.assetSnapshotTransformer;
    }
}
