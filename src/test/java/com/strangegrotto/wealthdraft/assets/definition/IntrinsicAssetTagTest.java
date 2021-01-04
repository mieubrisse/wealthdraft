package com.strangegrotto.wealthdraft.assets.definition;

import com.strangegrotto.wealthdraft.EnumDistinctnessVerifier;
import org.junit.Test;

public class IntrinsicAssetTagTest {
    @Test
    public void ensureNoDuplicates() {
        EnumDistinctnessVerifier.verifyDistinct(IntrinsicAssetTag.values(), IntrinsicAssetTag::getTagName);
    }
}
