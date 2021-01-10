package com.strangegrotto.wealthdraft.assets.definition;

import com.strangegrotto.wealthdraft.assetimpls.AssetType;
import com.strangegrotto.wealthdraft.assetimpls.bankaccount.ImmBankAccountAsset;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class AssetTest {
    @Test
    public void testGetTags() {
        String customTagName = "customTag";
        String customTagValue = "customValue";

        var asset = ImmBankAccountAsset.of("Test").withCustomTags(Map.of(
                customTagName,
                customTagValue
        ));
        var tags = asset.getTags();
        Assert.assertTrue(tags.containsKey(customTagName));
        Assert.assertEquals(customTagValue, tags.get(customTagName));

        Assert.assertTrue(tags.containsKey(IntrinsicAssetTag.ASSET_TYPE.getTagName()));
        Assert.assertEquals(AssetType.BANK_ACCOUNT.name(), tags.get(IntrinsicAssetTag.ASSET_TYPE.getTagName()));
    }
}
