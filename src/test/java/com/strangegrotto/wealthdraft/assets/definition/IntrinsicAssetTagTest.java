package com.strangegrotto.wealthdraft.assets.definition;

import com.strangegrotto.wealthdraft.EnumDistinctnessVerifier;
import com.strangegrotto.wealthdraft.assetimpls.AssetTypeTagValue;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.stream.Collectors;

public class IntrinsicAssetTagTest {
    @Test
    public void ensureNoDuplicates() {
        EnumDistinctnessVerifier.verifyDistinct(IntrinsicAssetTag.values(), IntrinsicAssetTag::getName);
    }
}
