package com.strangegrotto.wealthdraft.networth.projections;

import com.strangegrotto.wealthdraft.networth.assets.AssetType;

/**
 * Represents a change to an asset that yields a new version of the same type of asset
 */
public interface AssetChange {
    AssetType getApplicableType();
}
