package com.strangegrotto.wealthdraft.backend.assets.api.types;

import java.util.Map;

public class MockAsset implements Asset {
    private final String name;
    private final AssetType type;
    private final Map<String, String> tags;

    public MockAsset(String name, AssetType type, Map<String, String> tags) {
        this.name = name;
        this.type = type;
        this.tags = tags;
    }

    @Override
    public String getName() {
        return this.getName();
    }

    @Override
    public AssetType getType() {
        return this.getType();
    }

    @Override
    public Map<String, String> getTags() {
        return this.getTags();
    }
}
