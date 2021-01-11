package com.strangegrotto.wealthdraft.assets.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.strangegrotto.wealthdraft.assets.api.AssetsStore;
import com.strangegrotto.wealthdraft.assets.api.types.Asset;
import com.strangegrotto.wealthdraft.tagstores.custom.api.CustomTagStore;
import com.strangegrotto.wealthdraft.tagstores.intrinsic.IntrinsicAssetTag;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class SimpleAssetsStore implements AssetsStore {
    Map<String, Asset> assets;

    public SimpleAssetsStore(Map<String, ? extends Asset> assets) {
        this.assets = Map.copyOf(assets);
    }

    @Override
    public Map<String, Asset> getAssets() {
        return this.assets;
    }
}
