package com.strangegrotto.wealthdraft.backend.assethistory.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.strangegrotto.wealthdraft.Main;
import com.strangegrotto.wealthdraft.backend.assethistory.AssetsHistoryTestFiles;
import com.strangegrotto.wealthdraft.backend.assethistory.api.AssetHistoryStore;
import com.strangegrotto.wealthdraft.backend.assets.AssetDefinitionsTestFiles;
import com.strangegrotto.wealthdraft.backend.assets.impl.SimpleAssetsStoreFactoryTest;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class SerAssetsHistoryTest {
    @Test
    public void testValidDeserialization() throws IOException {
        var assetsStore = parseAssetsHistoryFile(Main.getObjectMapper(), AssetsHistoryTestFiles.EXAMPLE);
        Assert.assertEquals(
                ExpectedExampleAssetsHistory.EXPECTED_ASSETS_HISTORY,
                assetsStore.getHistory()
        );
    }

    @Test(expected = IllegalStateException.class)
    public void testErrorOnNonexistentAsset() throws IOException{
        parseAssetsHistoryFile(Main.getObjectMapper(), AssetsHistoryTestFiles.NONEXISTENT_ASSET);
    }

    @Test(expected = IllegalStateException.class)
    public void testErrorOnFutureHistory() throws IOException {
        parseAssetsHistoryFile(Main.getObjectMapper(), AssetsHistoryTestFiles.FUTURE_DATE);
    }

    private static AssetHistoryStore parseAssetsHistoryFile(ObjectMapper mapper, AssetsHistoryTestFiles testFile) throws IOException {
        var assetsStore = SimpleAssetsStoreFactoryTest.parseAssetsFile(mapper, AssetDefinitionsTestFiles.EXAMPLE);
        var factory = new SimpleAssetHistoryStoreFactory(mapper, assetsStore);
        return factory.create(testFile.getResource());
    }
}
