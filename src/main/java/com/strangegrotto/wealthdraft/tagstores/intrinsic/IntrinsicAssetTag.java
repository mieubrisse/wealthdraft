package com.strangegrotto.wealthdraft.tagstores.intrinsic;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum IntrinsicAssetTag {
    ASSET_TYPE("assetType");

    private final String name;

    IntrinsicAssetTag(String name) {
        this.name = name;
    }

    public String getTagName() {
        return name;
    }

    // TODO Delete??
    public static Set<String> getAllTagNames() {
        return Arrays.stream(IntrinsicAssetTag.values())
                .map(IntrinsicAssetTag::getTagName)
                .collect(Collectors.toSet());
    }
}
