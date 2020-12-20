package com.strangegrotto.wealthdraft.networth.history;

import com.fasterxml.jackson.core.JsonParseException;
import com.strangegrotto.wealthdraft.Main;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class AssetsWithHistoryDeserializerTest {
    @Test
    public void testValidDeserialization() throws IOException {
        AssetsWithHistory assetsWithHistory = parseAssetsFile(AssetsFiles.EXAMPLE);
        Assert.assertEquals(
                ExpectedExampleAssetsWithHistory.EXPECTED_ASSETS,
                assetsWithHistory.getAssets()
        );
        Assert.assertEquals(
                ExpectedExampleAssetsWithHistory.EXPECTED_ASSET_HISTORY,
                assetsWithHistory.getHistory()
        );
    }

    @Test(expected = JsonParseException.class)
    public void testErrorOnNonexistentAsset() throws IOException{
        parseAssetsFile(AssetsFiles.NONEXISTENT_ASSET);
    }

    @Test(expected = JsonParseException.class)
    public void testErrorOnFutureHistory() throws IOException {
        parseAssetsFile(AssetsFiles.FUTURE_DATE);
    }

    @Test(expected = JsonParseException.class)
    public void testErrorOnDisallowedTagValue() throws IOException {
        parseAssetsFile(AssetsFiles.DISALLOWED_TAG_VALUE);
    }

    private static AssetsWithHistory parseAssetsFile(AssetsFiles testFile) throws IOException {
        var mapper = Main.getObjectMapper();
        var assetsUrl = testFile.getResource();
        return mapper.readValue(assetsUrl, AssetsWithHistory.class);
    }
}
