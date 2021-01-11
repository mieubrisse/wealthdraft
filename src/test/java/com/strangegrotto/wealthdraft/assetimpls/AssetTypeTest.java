package com.strangegrotto.wealthdraft.assetimpls;

import com.strangegrotto.wealthdraft.EnumDistinctnessVerifier;
import com.strangegrotto.wealthdraft.assets.api.types.AssetType;
import org.junit.Test;

public class AssetTypeTest {
    @Test
    public void verifyDistinctSnapshotClasses() {
        EnumDistinctnessVerifier.verifyDistinct(
                AssetType.values(),
                AssetType::getSnapshotClass
        );
    }

    @Test
    public void verifyDistinctChangeClasses() {
        EnumDistinctnessVerifier.verifyDistinct(
                AssetType.values(),
                AssetType::getChangeClass
        );
    }
}
