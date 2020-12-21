package com.strangegrotto.wealthdraft.assets.definition;

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
