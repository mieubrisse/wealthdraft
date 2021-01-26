package com.strangegrotto.wealthdraft.backend.assets.impl;

import com.strangegrotto.wealthdraft.Main;
import com.strangegrotto.wealthdraft.backend.assets.AssetDefinitionsTestFiles;
import com.strangegrotto.wealthdraft.backend.assets.api.AssetsStore;
import com.strangegrotto.wealthdraft.backend.assets.api.types.Asset;
import com.strangegrotto.wealthdraft.backend.assets.api.types.AssetType;
import com.strangegrotto.wealthdraft.backend.tagstores.custom.CustomTagsTestFiles;
import com.strangegrotto.wealthdraft.backend.tagstores.custom.impl.SimpleCustomTagStoreFactoryTest;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class SimpleAssetsStoreFactoryTest {
    @Test
    public void testValidDeserialization() throws IOException {
        var assetsStore = parseAssetsFile(AssetDefinitionsTestFiles.EXAMPLE);
        Assert.assertEquals(
                ExpectedExampleAssetDefinitions.EXPECTED_ASSETS,
                assetsStore.getAssets()
        );
    }

    @Test
    public void testDeserializingEveryAsset() throws IOException {
        var assetsStore = parseAssetsFile(AssetDefinitionsTestFiles.EVERY_ASSET_TYPE);
        var assetsMap = assetsStore.getAssets();
        var assets = assetsMap.values();

        var expectedDistinctAssets = AssetType.values().length;

        // Verify that we do indeed have one of every class
        var distinctAssetClasses = assets.stream()
                .map(Asset::getType)
                .distinct()
                .count();
        Assert.assertEquals(expectedDistinctAssets, distinctAssetClasses);
    }

    @Test
    public void testDefaultTags() throws IOException {
        var assetsStore = parseAssetsFile(AssetDefinitionsTestFiles.TEST_DEFAULT_TAGS);
        var assets = assetsStore.getAssets();
        var myAsset = assets.get("myAsset");
        var myAssetCustomTags = myAsset.getCustomTags();

        var tagName = "tagWithDefault";

        Assert.assertTrue(myAssetCustomTags.containsKey(tagName));
        Assert.assertEquals("someValue", myAssetCustomTags.get(tagName));
    }

    @Test
    public void unspecifiedTagsIsEmptyMap() throws IOException {
        var definitions = parseAssetsFile(AssetDefinitionsTestFiles.UNSPECIFIED_TAGS_IS_EMPTY_MAP);
        var asset = definitions.getAssets().get(ExpectedExampleAssetDefinitions.RETIREMENT_ACCOUNT_ID);
        Assert.assertEquals(asset.getCustomTags().size(), 0);
    }

    @Test(expected = IllegalStateException.class)
    public void testErrorOnInvalidTagValue() throws IOException {
        parseAssetsFile(AssetDefinitionsTestFiles.DISALLOWED_TAG_VALUES);
    }

    @Test(expected = IllegalStateException.class)
    public void testErrorOnCollisionWithIntrinsicTag() throws IOException {
        parseAssetsFile(AssetDefinitionsTestFiles.INTRINSIC_TAG_COLLISION);
    }

    @Test(expected = IllegalStateException.class)
    public void testErrorOnUnrecognizedTag() throws IOException {
        parseAssetsFile(AssetDefinitionsTestFiles.UNRECOGNIZED_TAG);
    }

    @Test(expected = IllegalStateException.class)
    public void testErrorOnMissingRequiredTag() throws IOException {
        parseAssetsFile(AssetDefinitionsTestFiles.MISSING_REQUIRED_TAG);
    }

    private static AssetsStore parseAssetsFile(AssetDefinitionsTestFiles testFile) throws IOException {
        var mapper = Main.getObjectMapper();
        var assetsUrl = testFile.getResource();

        var customTagStore = SimpleCustomTagStoreFactoryTest.parseCustomTagsFile(CustomTagsTestFiles.EXAMPLE);
        var factory = new SimpleAssetsStoreFactory(mapper, customTagStore);
        return factory.create(testFile.getResource());
    }
}
