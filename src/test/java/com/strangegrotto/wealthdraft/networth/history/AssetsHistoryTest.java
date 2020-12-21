package com.strangegrotto.wealthdraft.networth.history;

import com.strangegrotto.wealthdraft.Main;
import com.strangegrotto.wealthdraft.assets.definition.AssetDefinitions;
import com.strangegrotto.wealthdraft.assets.definition.AssetDefinitionsTestFiles;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class AssetsHistoryTest {
    @Test
    public void testValidDeserialization() throws IOException {
        AssetsHistory assetsHistory = parseAssetsHistoryFile(AssetsHistoryTestFiles.EXAMPLE);
        Assert.assertEquals(
                ExpectedExampleAssetsHistory.EXPECTED_ASSETS_HISTORY,
                assetsHistory.getHistory()
        );
    }

    @Test(expected = IllegalStateException.class)
    public void testErrorOnNonexistentAsset() throws IOException{
        parseAssetsHistoryFile(AssetsHistoryTestFiles.NONEXISTENT_ASSET);
    }

    @Test(expected = IllegalStateException.class)
    public void testErrorOnFutureHistory() throws IOException {
        parseAssetsHistoryFile(AssetsHistoryTestFiles.FUTURE_DATE);
    }

    private static AssetsHistory parseAssetsHistoryFile(AssetsHistoryTestFiles testFile) throws IOException {
        var mapper = Main.getObjectMapper();
        var assetsUrl = AssetDefinitionsTestFiles.EXAMPLE.getResource();
        var assetDefinitions = mapper.readValue(assetsUrl, AssetDefinitions.class);
        Main.addDeserializersNeedingAssets(mapper, assetDefinitions.getAssets());

        var assetsHistoryUrl = testFile.getResource();
        return mapper.readValue(assetsHistoryUrl, AssetsHistory.class);
    }
}
