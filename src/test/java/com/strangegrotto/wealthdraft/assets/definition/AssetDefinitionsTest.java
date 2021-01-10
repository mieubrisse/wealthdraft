package com.strangegrotto.wealthdraft.assets.definition;

import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import com.strangegrotto.wealthdraft.Main;
import com.strangegrotto.wealthdraft.assetimpls.AssetType;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class AssetDefinitionsTest {
    @Test
    public void testValidDeserialization() throws IOException {
        AssetDefinitions definitions = parseAssetsFile(AssetDefinitionsTestFiles.EXAMPLE);
        Assert.assertEquals(
                ExpectedExampleAssetDefinitions.EXPECTED_ASSETS,
                definitions.getAssets()
        );
        Assert.assertEquals(
                ExpectedExampleAssetDefinitions.EXPECTED_CUSTOM_TAGS,
                definitions.getCustomTags()
        );
    }

    @Test
    public void testDeserializingEveryAsset() throws IOException {
        var definitions = parseAssetsFile(AssetDefinitionsTestFiles.EVERY_ASSET_TYPE);
        var assetsMap = definitions.getAssets();
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
    public void unspecifiedTagsIsEmptyMap() throws IOException {
        var definitions = parseAssetsFile(AssetDefinitionsTestFiles.UNSPECIFIED_TAGS_IS_EMPTY_MAP);
        var asset = definitions.getAssets().get(ExpectedExampleAssetDefinitions.RETIREMENT_ACCOUNT_ID);
        Assert.assertEquals(asset.getCustomTags().size(), 0);
    }

    @Test(expected = ValueInstantiationException.class)
    public void testErrorOnInvalidTagValue() throws IOException {
        parseAssetsFile(AssetDefinitionsTestFiles.DISALLOWED_TAG_VALUES);
    }

    @Test(expected = ValueInstantiationException.class)
    public void testErrorOnCollisionWithIntrinsicTag() throws IOException {
        parseAssetsFile(AssetDefinitionsTestFiles.INTRINSIC_TAG_COLLISION);
    }

    @Test(expected = ValueInstantiationException.class)
    public void testErrorOnUnrecognizedTag() throws IOException {
        parseAssetsFile(AssetDefinitionsTestFiles.UNRECOGNIZED_TAG);
    }

    @Test(expected = ValueInstantiationException.class)
    public void testErrorOnMissingRequiredTag() throws IOException {
        parseAssetsFile(AssetDefinitionsTestFiles.MISSING_REQUIRED_TAG);
    }

    private static AssetDefinitions parseAssetsFile(AssetDefinitionsTestFiles testFile) throws IOException {
        var mapper = Main.getObjectMapper();
        var assetsUrl = testFile.getResource();
        return mapper.readValue(assetsUrl, AssetDefinitions.class);
    }
}
