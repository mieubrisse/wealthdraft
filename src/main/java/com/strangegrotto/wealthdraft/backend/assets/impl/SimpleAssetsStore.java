package com.strangegrotto.wealthdraft.backend.assets.impl;

import com.strangegrotto.wealthdraft.backend.assets.api.AssetsStore;
import com.strangegrotto.wealthdraft.backend.assets.api.types.Asset;

import java.util.Map;

class SimpleAssetsStore implements AssetsStore {
    Map<String, Asset> assets;

    public SimpleAssetsStore(Map<String, ? extends Asset> assets) {
        this.assets = Map.copyOf(assets);
    }

    @Override
    public Map<String, Asset> getAssets() {
        return this.assets;
    }
}
