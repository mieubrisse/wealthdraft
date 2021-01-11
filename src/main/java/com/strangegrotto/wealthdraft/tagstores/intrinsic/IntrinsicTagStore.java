package com.strangegrotto.wealthdraft.tagstores.intrinsic;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class IntrinsicTagStore {
    public Set<String> getTags() {
        return Arrays.stream(IntrinsicAssetTag.values())
                .map(IntrinsicAssetTag::getTagName)
                .collect(Collectors.toSet());
    }
}
