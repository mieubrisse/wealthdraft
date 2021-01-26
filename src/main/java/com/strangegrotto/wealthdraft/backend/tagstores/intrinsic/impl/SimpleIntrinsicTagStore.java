package com.strangegrotto.wealthdraft.backend.tagstores.intrinsic.impl;

import com.strangegrotto.wealthdraft.backend.tagstores.intrinsic.IntrinsicAssetTag;
import com.strangegrotto.wealthdraft.backend.tagstores.intrinsic.api.IntrinsicTagStore;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public enum SimpleIntrinsicTagStore implements IntrinsicTagStore {
    INSTANCE;

    private static final Map<String, Set<String>> TAGS = Arrays.stream(IntrinsicAssetTag.values())
            .collect(Collectors.toMap(
                    IntrinsicAssetTag::getTagName,
                    IntrinsicAssetTag::getAllowedValues
            ));

    // Map of tagName -> allowed tag values
    public Map<String, Set<String>> getTags() {
        return TAGS;
    }
}
