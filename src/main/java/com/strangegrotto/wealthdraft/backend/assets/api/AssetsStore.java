package com.strangegrotto.wealthdraft.backend.assets.api;

import com.strangegrotto.wealthdraft.backend.assets.api.types.Asset;

import java.util.Map;

public interface AssetsStore {
    Map<String, Asset> getAssets();
}
