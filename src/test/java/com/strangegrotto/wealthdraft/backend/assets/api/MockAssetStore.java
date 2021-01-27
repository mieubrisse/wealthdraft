package com.strangegrotto.wealthdraft.backend.assets.api;

import com.strangegrotto.wealthdraft.backend.assethistory.api.types.AssetSnapshot;
import com.strangegrotto.wealthdraft.backend.assets.api.types.Asset;

import java.util.Map;

public class MockAssetStore implements AssetsStore {
    private final Map<String, Asset> assets;

    public MockAssetStore(Map<String, Asset> assets) {
        this.assets = assets;
    }

    @Override
    public Map<String, Asset> getAssets() {
        return this.assets;
    }
}
