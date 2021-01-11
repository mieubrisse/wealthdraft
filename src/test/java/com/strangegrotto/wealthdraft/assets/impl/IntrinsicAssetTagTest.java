package com.strangegrotto.wealthdraft.assets.impl;

import com.strangegrotto.wealthdraft.EnumDistinctnessVerifier;
import com.strangegrotto.wealthdraft.tagstores.intrinsic.IntrinsicAssetTag;
import org.junit.Test;

public class IntrinsicAssetTagTest {
    @Test
    public void ensureNoDuplicates() {
        EnumDistinctnessVerifier.verifyDistinct(IntrinsicAssetTag.values(), IntrinsicAssetTag::getTagName);
    }
}
