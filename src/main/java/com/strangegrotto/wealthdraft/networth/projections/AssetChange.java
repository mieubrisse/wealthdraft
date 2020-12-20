package com.strangegrotto.wealthdraft.networth.projections;

import com.strangegrotto.wealthdraft.errors.ValOrGerr;
import com.strangegrotto.wealthdraft.networth.assets.AssetSnapshot;

/**
 * Represents a change to an asset that yields a new version of the same type of asset
 */
public interface AssetChange<T extends AssetSnapshot> {
    ValOrGerr<T> apply(T snapshot);
}
