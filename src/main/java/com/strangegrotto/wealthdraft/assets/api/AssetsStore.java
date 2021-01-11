package com.strangegrotto.wealthdraft.assets.api;

import com.strangegrotto.wealthdraft.assets.api.types.Asset;
import com.strangegrotto.wealthdraft.assets.impl.SerAsset;

import java.util.Map;

public interface AssetsStore {
    Map<String, Asset> getAssets();
}
