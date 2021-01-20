package com.strangegrotto.wealthdraft.networth.history;

import com.strangegrotto.wealthdraft.Main;
import com.strangegrotto.wealthdraft.backend.assethistory.impl.SerAssetsHistory;
import com.strangegrotto.wealthdraft.backend.assets.impl.AssetDefinitions;
import com.strangegrotto.wealthdraft.backend.assets.impl.AssetDefinitionsTestFiles;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class SerAssetsHistoryTest {
    @Test
    public void testValidDeserialization() throws IOException {
        SerAssetsHistory assetsHistory = parseAssetsHistoryFile(AssetsHistoryTestFiles.EXAMPLE);
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

    private static SerAssetsHistory parseAssetsHistoryFile(AssetsHistoryTestFiles testFile) throws IOException {
        var mapper = Main.getObjectMapper();
        var assetsUrl = AssetDefinitionsTestFiles.EXAMPLE.getResource();
        var assetDefinitions = mapper.readValue(assetsUrl, AssetDefinitions.class);
        Main.addDeserializersNeedingAssetDefs(mapper, assetDefinitions);

        var assetsHistoryUrl = testFile.getResource();
        return mapper.readValue(assetsHistoryUrl, SerAssetsHistory.class);
    }
}
