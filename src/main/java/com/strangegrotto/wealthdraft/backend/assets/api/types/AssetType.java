package com.strangegrotto.wealthdraft.backend.assets.api.types;

import com.strangegrotto.wealthdraft.assetimpls.bankaccount.BankAccountAssetChange;
import com.strangegrotto.wealthdraft.assetimpls.bankaccount.BankAccountAssetSnapshot;
import com.strangegrotto.wealthdraft.assetimpls.stock.StockAssetChange;
import com.strangegrotto.wealthdraft.assetimpls.stock.StockAssetSnapshot;
import com.strangegrotto.wealthdraft.backend.assethistory.api.types.AssetSnapshot;
import com.strangegrotto.wealthdraft.backend.projections.api.types.AssetChange;

public enum AssetType {
    // TODO need to remove the snapshot/change from this
    BANK_ACCOUNT(BankAccountAssetSnapshot.class, BankAccountAssetChange.class),
    STOCK(StockAssetSnapshot.class, StockAssetChange.class);

    private final Class<? extends AssetSnapshot<?>> snapshotClass;
    private final Class<? extends AssetChange> changeClass;

    <SNAPSHOT extends AssetSnapshot<CHANGE>, CHANGE extends AssetChange> AssetType(
            Class<SNAPSHOT> snapshotClass,
            Class<CHANGE> changeClass) {
        this.snapshotClass = snapshotClass;
        this.changeClass = changeClass;
    }

    public Class<? extends AssetSnapshot<?>> getSnapshotClass() {
        return snapshotClass;
    }

    public Class<? extends AssetChange> getChangeClass() {
        return changeClass;
    }
}
