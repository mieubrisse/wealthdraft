package com.strangegrotto.wealthdraft.assetimpls;

import com.strangegrotto.wealthdraft.EnumDistinctnessVerifier;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class AssetTypeTagValueTest {
    @Test
    public void ensureNoDuplicates() {
        EnumDistinctnessVerifier.verifyDistinct(AssetTypeTagValue.values(), AssetTypeTagValue::getValue);
    }
}
