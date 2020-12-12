package com.strangegrotto.wealthdraft.networth;

public interface AssetProjector<T extends AssetSnapshot> {
    T getNextSnapshot(T currentSnapshot);
}
