package com.strangegrotto.wealthdraft.assets.definition;

import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import com.strangegrotto.wealthdraft.Main;
import com.strangegrotto.wealthdraft.assetimpls.AssetTypeTagValue;
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

        var expectedDistinctAssets = AssetTypeTagValue.values().length;

        // Verify that we do indeed have one of every class
        var distinctAssetClasses = assets.stream()
                .map(Object::getClass)
                .distinct()
                .count();
        Assert.assertEquals(expectedDistinctAssets, distinctAssetClasses);

        // Verify no assets are reusing snapshot classes
        var distinctSnapshotClasses = assets.stream()
                .map(Asset::getSnapshotType)
                .distinct()
                .count();
        Assert.assertEquals(expectedDistinctAssets, distinctSnapshotClasses);

        // Verify no assets are reusing change classes
        var distinctChangeClasses = assets.stream()
                .map(Asset::getChangeType)
                .distinct()
                .count();
        Assert.assertEquals(expectedDistinctAssets, distinctChangeClasses);

        // Verify each asset has a unique asset type tag value
        var distinctAssetTypeTags = assets.stream()
                .map(Asset::getAssetTypeTagValue)
                .distinct()
                .count();
        Assert.assertEquals(expectedDistinctAssets, distinctAssetTypeTags);
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

    private static AssetDefinitions parseAssetsFile(AssetDefinitionsTestFiles testFile) throws IOException {
        var mapper = Main.getObjectMapper();
        var assetsUrl = testFile.getResource();
        return mapper.readValue(assetsUrl, AssetDefinitions.class);
    }
}
