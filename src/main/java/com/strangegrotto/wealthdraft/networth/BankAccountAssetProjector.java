package com.strangegrotto.wealthdraft.networth;

public class BankAccountAssetProjector implements AssetProjector<BankAccountAssetSnapshot> {
    private final double defaultGrowthRate;

    public BankAccountAssetProjector(double defaultGrowthRate) {
        this.defaultGrowthRate = defaultGrowthRate;
    }

    @Override
    public BankAccountAssetSnapshot getNextSnapshot(BankAccountAssetSnapshot currentSnapshot) {
        // TODO fix
        return currentSnapshot;
    }
}
