package com.strangegrotto.wealthdraft.backend.assets.api;

import com.strangegrotto.wealthdraft.EnumDistinctnessVerifier;
import com.strangegrotto.wealthdraft.backend.assets.api.types.AssetType;
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
