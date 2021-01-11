package com.strangegrotto.wealthdraft.tagstores.intrinsic;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class IntrinsicTagStore {
    private static final Map<String, Set<String>> TAGS = Arrays.stream(IntrinsicAssetTag.values())
            .collect(Collectors.toMap(
                    IntrinsicAssetTag::getTagName,
                    IntrinsicAssetTag::getAllowedValues
            ));

    public Map<String, Set<String>> getTags() {
        return TAGS;
    }
}
