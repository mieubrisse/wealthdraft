package com.strangegrotto.wealthdraft.networth;

import com.strangegrotto.wealthdraft.networth.projections.AssetChange;

public enum AssetType {
    BANK_ACCOUNT(BankAccountAssetSnapshot.class, BankAccountAssetChange.class);

    private final Class<? extends AssetSnapshot> snapshotType;
    private final Class<? extends AssetChange<? extends AssetSnapshot>> changeType;

    <T extends AssetSnapshot> AssetType(Class<T> snapshotType, Class<? extends AssetChange<T>> changeType) {
        this.snapshotType = snapshotType;
        this.changeType = changeType;
    }

    public Class<? extends AssetSnapshot> getSnapshotType() {
        return this.snapshotType;
    }

    public Class<? extends AssetChange<? extends AssetSnapshot>> getChangeType() {
        return changeType;
    }
}
