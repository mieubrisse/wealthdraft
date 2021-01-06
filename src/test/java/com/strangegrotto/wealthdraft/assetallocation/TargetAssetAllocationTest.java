package com.strangegrotto.wealthdraft.assetallocation;

import com.fasterxml.jackson.databind.type.CollectionType;
import com.strangegrotto.wealthdraft.Main;
import com.strangegrotto.wealthdraft.assets.definition.AssetDefinitions;
import com.strangegrotto.wealthdraft.assets.definition.AssetDefinitionsTestFiles;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TargetAssetAllocationTest {
    @Test
    public void testValidDeserialization() throws IOException {
        var targetAssetAllocations = parseAssetAllocationsFile(TargetAssetAllocationsTestFiles.EXAMPLE);
        Assert.assertEquals(targetAssetAllocations, ExpectedExampleTargetAssetAllocations.ASSET_ALLOCATIONS);
    }


    private static TargetAssetAllocations parseAssetAllocationsFile(TargetAssetAllocationsTestFiles testFile) throws IOException {
        var mapper = Main.getObjectMapper();
        var assetAllocationsUrl = testFile.getResource();
        return mapper.readValue(assetAllocationsUrl, TargetAssetAllocations.class);
    }
}
