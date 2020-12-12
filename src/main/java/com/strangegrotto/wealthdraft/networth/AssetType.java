package com.strangegrotto.wealthdraft.networth;

import com.strangegrotto.wealthdraft.errors.ValOrGerr;
import com.strangegrotto.wealthdraft.networth.projections.AssetChange;

import java.util.function.BiFunction;

public enum AssetType {
    BANK_ACCOUNT(BankAccountAssetSnapshot.class, BankAccountAssetChange.class);

    private final Class<? extends AssetSnapshot> snapshotType;
    private final Class<? extends AssetChange<? extends AssetSnapshot>> changeType;
    private final BiFunction<? extends AssetSnapshot, AssetChange<? extends AssetSnapshot>, ValOrGerr<? extends AssetSnapshot extends AssetSnapshot>> assetChangeApplicator;

    <T extends AssetSnapshot> AssetType(
            Class<T> snapshotType,
            Class<? extends AssetChange<T>> changeType,
            BiFunction<T, AssetChange<T>, ValOrGerr<T>> assetChangeApplicator) {
        this.snapshotType = snapshotType;
        this.changeType = changeType;
        this.assetChangeApplicator = assetChangeApplicator;
    }

    public Class<? extends AssetSnapshot> getSnapshotType() {
        return this.snapshotType;
    }

    public Class<? extends AssetChange<? extends AssetSnapshot>> getChangeType() {
        return changeType;
    }

    public <T extends AssetSnapshot> BiFunction<T, AssetChange<T>, ValOrGerr<T>> getAssetChangeApplicator() {
        return (snapshot, change) -> change.apply(snapshot);
    }
}
