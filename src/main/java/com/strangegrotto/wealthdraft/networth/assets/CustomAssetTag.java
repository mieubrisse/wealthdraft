package com.strangegrotto.wealthdraft.networth.assets;

class CustomAssetTag implements AssetTag {
    private final String name;

    public CustomAssetTag(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
