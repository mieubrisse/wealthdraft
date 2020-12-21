package com.strangegrotto.wealthdraft.assets.definition;

import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import com.strangegrotto.wealthdraft.Main;
import com.strangegrotto.wealthdraft.networth.history.AssetsHistoryFiles;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class AssetDefinitionsTest {
    @Test
    public void testValidDeserialization() throws IOException {
        AssetDefinitions definitions = parseAssetsFile(AssetDefinitionsFiles.EXAMPLE);
        Assert.assertEquals(
                ExpectedExampleAssetDefinitions.EXPECTED_ASSETS,
                definitions.getAssets()
        );
        Assert.assertEquals(
                ExpectedExampleAssetDefinitions.EXPECTED_CUSTOM_TAGS,
                definitions.getCustomTags()
        );
    }

    @Test(expected = ValueInstantiationException.class)
    public void testErrorOnInvalidTagValue() throws IOException {
        parseAssetsFile(AssetDefinitionsFiles.DISALLOWED_TAG_VALUES);
    }

    @Test(expected = ValueInstantiationException.class)
    public void testErrorOnCollisionWithIntrinsicTag() throws IOException {
        parseAssetsFile(AssetDefinitionsFiles.INTRINSIC_TAG_COLLISION);
    }

    @Test(expected = ValueInstantiationException.class)
    public void testErrorOnUnrecognizedTag() throws IOException {
        parseAssetsFile(AssetDefinitionsFiles.UNRECOGNIZED_TAG);
    }

    private static AssetDefinitions parseAssetsFile(AssetDefinitionsFiles testFile) throws IOException {
        var mapper = Main.getObjectMapper();
        var assetsUrl = testFile.getResource();
        return mapper.readValue(assetsUrl, AssetDefinitions.class);
    }
}
