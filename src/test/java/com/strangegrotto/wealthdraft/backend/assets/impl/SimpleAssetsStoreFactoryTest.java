package com.strangegrotto.wealthdraft.backend.assets.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.strangegrotto.wealthdraft.Main;
import com.strangegrotto.wealthdraft.backend.assets.AssetDefinitionsTestFiles;
import com.strangegrotto.wealthdraft.backend.assets.api.AssetsStore;
import com.strangegrotto.wealthdraft.backend.assets.api.types.Asset;
import com.strangegrotto.wealthdraft.backend.assets.api.types.AssetType;
import com.strangegrotto.wealthdraft.backend.tags.custom.CustomTagsTestFiles;
import com.strangegrotto.wealthdraft.backend.tags.custom.impl.ExpectedExampleCustomTags;
import com.strangegrotto.wealthdraft.backend.tags.custom.impl.SimpleCustomTagStoreFactory;
import com.strangegrotto.wealthdraft.backend.tags.custom.impl.SimpleCustomTagStoreFactoryTest;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class SimpleAssetsStoreFactoryTest {
    @Test
    public void testValidDeserialization() throws IOException {
        var assetsStore = parseAssetsFile(Main.getObjectMapper(), AssetDefinitionsTestFiles.EXAMPLE);
        Assert.assertEquals(
                ExpectedExampleAssetDefinitions.EXPECTED_ASSETS,
                assetsStore.getAssets()
        );
    }

    @Test
    public void testDeserializingEveryAsset() throws IOException {
        var assetsStore = parseAssetsFile(Main.getObjectMapper(), AssetDefinitionsTestFiles.EVERY_ASSET_TYPE);
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
        var assetsStore = parseAssetsFile(Main.getObjectMapper(), AssetDefinitionsTestFiles.TEST_DEFAULT_TAGS);
        var assets = assetsStore.getAssets();
        var myAsset = assets.get("myAsset");
        var actualTagVal = myAsset.getTags().get(ExpectedExampleCustomTags.DOM_OR_INTL_TAG);

        var tagDef = ExpectedExampleCustomTags.EXPECTED_CUSTOM_TAGS
                .get(ExpectedExampleCustomTags.DOM_OR_INTL_TAG);
        Assert.assertTrue(tagDef.getDefaultValue().isPresent());
        var expectedTagVal = tagDef.getDefaultValue().get();
        Assert.assertEquals(expectedTagVal, actualTagVal);
    }

    @Test(expected = IllegalStateException.class)
    public void testErrorOnInvalidTagValue() throws IOException {
        parseAssetsFile(Main.getObjectMapper(), AssetDefinitionsTestFiles.DISALLOWED_TAG_VALUES);
    }

    @Test(expected = IllegalStateException.class)
    public void testErrorOnCollisionWithIntrinsicTag() throws IOException {
        parseAssetsFile(Main.getObjectMapper(), AssetDefinitionsTestFiles.INTRINSIC_TAG_COLLISION);
    }

    @Test(expected = IllegalStateException.class)
    public void testErrorOnUnrecognizedTag() throws IOException {
        parseAssetsFile(Main.getObjectMapper(), AssetDefinitionsTestFiles.UNRECOGNIZED_TAG);
    }

    @Test(expected = IllegalStateException.class)
    public void testErrorOnMissingRequiredTag() throws IOException {
        parseAssetsFile(Main.getObjectMapper(), AssetDefinitionsTestFiles.MISSING_REQUIRED_TAG);
    }

    private static AssetsStore parseAssetsFile(ObjectMapper mapper, AssetDefinitionsTestFiles testFile) throws IOException {
        // TODO switch to using mocks
        var customTagStore = SimpleCustomTagStoreFactoryTest.parseCustomTagsFile(
                mapper,
                CustomTagsTestFiles.EXAMPLE
        );
        var factory = new SimpleAssetsStoreFactory(mapper, customTagStore);
        return factory.create(testFile.getResource());
    }
}
