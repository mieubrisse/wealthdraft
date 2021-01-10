package com.strangegrotto.wealthdraft.assetimpls;

import com.strangegrotto.wealthdraft.assetimpls.bankaccount.BankAccountAssetChange;
import com.strangegrotto.wealthdraft.assetimpls.bankaccount.BankAccountAssetSnapshot;
import com.strangegrotto.wealthdraft.assetimpls.stock.StockAssetChange;
import com.strangegrotto.wealthdraft.assetimpls.stock.StockAssetSnapshot;
import com.strangegrotto.wealthdraft.assets.temporal.AssetChange;
import com.strangegrotto.wealthdraft.assets.temporal.AssetSnapshot;

public enum AssetType {
    BANK_ACCOUNT(BankAccountAssetSnapshot.class, BankAccountAssetChange.class),
    STOCK(StockAssetSnapshot.class, StockAssetChange.class);

    private final Class<?> snapshotClass;
    private final Class<?> changeClass;

    <SNAPSHOT extends AssetSnapshot<CHANGE>, CHANGE extends AssetChange> AssetType(
            Class<SNAPSHOT> snapshotClass,
            Class<CHANGE> changeClass) {
        this.snapshotClass = snapshotClass;
        this.changeClass = changeClass;
    }

    public Class<?> getSnapshotClass() {
        return snapshotClass;
    }

    public Class<?> getChangeClass() {
        return changeClass;
    }
}
