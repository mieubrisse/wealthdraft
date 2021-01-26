package com.strangegrotto.wealthdraft.backend.tagstores.intrinsic;

import com.strangegrotto.wealthdraft.EnumDistinctnessVerifier;
import org.junit.Test;

public class IntrinsicAssetTagTest {
    @Test
    public void ensureNoDuplicates() {
        EnumDistinctnessVerifier.verifyDistinct(IntrinsicAssetTag.values(), IntrinsicAssetTag::getTagName);
    }
}
