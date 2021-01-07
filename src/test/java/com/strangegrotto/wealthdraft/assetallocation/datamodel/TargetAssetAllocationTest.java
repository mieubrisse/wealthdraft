package com.strangegrotto.wealthdraft.assetallocation.datamodel;

import com.strangegrotto.wealthdraft.Main;
import com.strangegrotto.wealthdraft.assetallocation.ExpectedExampleTargetAssetAllocations;
import com.strangegrotto.wealthdraft.assetallocation.TargetAssetAllocationsTestFiles;
import com.strangegrotto.wealthdraft.assetallocation.datamodel.TargetAssetAllocations;
import com.strangegrotto.wealthdraft.assets.definition.ExpectedExampleAssetDefinitions;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Set;

public class TargetAssetAllocationTest {
    @Test
    public void testValidDeserialization() throws IOException {
        var targetAssetAllocations = parseAssetAllocationsFile(TargetAssetAllocationsTestFiles.EXAMPLE);
        Assert.assertEquals(targetAssetAllocations, ExpectedExampleTargetAssetAllocations.ASSET_ALLOCATIONS);
    }

    @Test
    public void testAllSetMathOperators() throws IOException {
        var targetAssetAllocations = parseAssetAllocationsFile(TargetAssetAllocationsTestFiles.ALL_SET_MATH_OPERATORS);
        var megaFilter = targetAssetAllocations.getFilters().get("megaFilter");
        var filteredAssetIds = megaFilter.apply(
                ExpectedExampleAssetDefinitions.EXPECTED_ASSETS,
                ExpectedExampleAssetDefinitions.EXPECTED_ASSETS.keySet()
        );
        var expectedAssetIds = Set.of(
                ExpectedExampleAssetDefinitions.BROKERAGE_ACCOUNT_ID,
                ExpectedExampleAssetDefinitions.BITCOIN_HOLDING_ID
        );
        Assert.assertEquals(expectedAssetIds, filteredAssetIds);
    }

    private static TargetAssetAllocations parseAssetAllocationsFile(TargetAssetAllocationsTestFiles testFile) throws IOException {
        var mapper = Main.getObjectMapper();
        var assetAllocationsUrl = testFile.getResource();
        return mapper.readValue(assetAllocationsUrl, TargetAssetAllocations.class);
    }
}
