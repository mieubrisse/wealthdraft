package com.strangegrotto.wealthdraft.assetallocation.datamodel.filters;

import com.strangegrotto.wealthdraft.assetimpls.AssetType;
import com.strangegrotto.wealthdraft.assets.definition.Asset;
import com.strangegrotto.wealthdraft.assets.definition.ImmAsset;
import com.strangegrotto.wealthdraft.assets.definition.ImmCustomTagDefinition;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;
import java.util.Set;

public class NegationAssetFilterTest {
    @Test
    public void testVanilla() {
        var assetId1 = "asset1";
        var assetId2 = "asset2";
        var assetId3 = "asset3";

        var tagName = "tagName";
        var tagValue = "tagValue";

        var customTags = Map.of(
                tagName, ImmCustomTagDefinition.builder().build()
        );

        var assets = Map.<String, Asset>of(
                assetId1, ImmAsset.of("Asset 1", AssetType.BANK_ACCOUNT).withCustomTags(Map.of(
                        tagName, tagValue
                )),
                assetId2, ImmAsset.of("Asset 2", AssetType.BANK_ACCOUNT),
                assetId3, ImmAsset.of("Asset 3", AssetType.BANK_ACCOUNT)
        );

        var tagFilter = ImmTagAssetFilter.of(customTags, tagName, tagValue);

        var negatedFilter = ImmNegationAssetFilter.of(tagFilter);

        var expected = Set.of(assetId2, assetId3);
        var result = negatedFilter.apply(assets, assets.keySet());

        Assert.assertEquals(expected, result);
    }
}
