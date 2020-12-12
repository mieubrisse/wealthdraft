package com.strangegrotto.wealthdraft.networth.projections;

import com.strangegrotto.wealthdraft.errors.ValOrGerr;
import com.strangegrotto.wealthdraft.networth.AssetType;

/**
 * Represents a change to an asset that yields a new version of the same type of asset
 * @param <T> The type of snapshot that the asset change operates on
 */
public interface AssetChange {
    AssetType getApplicableType();

    ValOrGerr<T> apply(T snapshot);
}
