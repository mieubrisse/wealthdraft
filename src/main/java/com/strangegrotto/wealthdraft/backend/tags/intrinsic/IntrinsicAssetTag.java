package com.strangegrotto.wealthdraft.backend.tags.intrinsic;

import com.strangegrotto.wealthdraft.backend.assets.api.types.AssetType;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public enum IntrinsicAssetTag {
    ASSET_TYPE("assetType", Arrays.stream(AssetType.values()).map(Enum::name).collect(Collectors.toSet()));

    private final String tagName;
    private final Set<String> allowedValues;

    IntrinsicAssetTag(String tagName, Set<String> allowedValues) {
        this.tagName = tagName;
        this.allowedValues = allowedValues;
    }

    public String getTagName() {
        return tagName;
    }

    public Set<String> getAllowedValues() {
        return allowedValues;
    }

    public static Map<String, Set<String>> getTagNamesToAllowedValues() {
        return Arrays.stream(IntrinsicAssetTag.values())
                .collect(Collectors.toMap(
                        IntrinsicAssetTag::getTagName,
                        IntrinsicAssetTag::getAllowedValues
                ));
    }
}
