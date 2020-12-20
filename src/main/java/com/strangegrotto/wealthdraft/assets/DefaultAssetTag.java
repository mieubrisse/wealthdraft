package com.strangegrotto.wealthdraft.assets;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DefaultAssetTag implements AssetTag {
    ASSET_TYPE("assetType");

    private final String name;

    DefaultAssetTag(String name) {
        this.name = name;
    }

    @Override
    @JsonValue
    public String getName() {
        return name;
    }
}
