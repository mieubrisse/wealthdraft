package com.strangegrotto.wealthdraft.networth;

import com.strangegrotto.wealthdraft.errors.ValOrGerr;

/**
 * Represents a change to an asset that yields a new version of the same type of asset
 * @param <T> The type of snapshot that the asset change operates on
 */
public interface AssetChange<T extends AssetSnapshot> {
    ValOrGerr<T> apply(T snapshot);
}
