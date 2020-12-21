package com.strangegrotto.wealthdraft.assets.definition;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum IntrinsicAssetTag {
    ASSET_TYPE("assetType");

    private final String name;

    IntrinsicAssetTag(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Set<String> getAllNames() {
        return Arrays.stream(IntrinsicAssetTag.values())
                .map(IntrinsicAssetTag::getName)
                .collect(Collectors.toSet());
    }
}
