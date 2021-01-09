package com.strangegrotto.wealthdraft.assetallocation.datamodel;

import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import com.strangegrotto.wealthdraft.Main;
import com.strangegrotto.wealthdraft.assetallocation.ExpectedExampleTargetAssetAllocations;
import com.strangegrotto.wealthdraft.assetallocation.TargetAssetAllocationsTestFiles;
import com.strangegrotto.wealthdraft.assets.definition.ExpectedExampleAssetDefinitions;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Set;

public class TargetAssetAllocationsTest {
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

    @Test(expected = ValueInstantiationException.class)
    public void testErrorOnNonexistentNumeratorFilter() throws IOException {
        parseAssetAllocationsFile(TargetAssetAllocationsTestFiles.NONEXISTENT_NUMERATOR_FILTER);
    }

    @Test(expected = ValueInstantiationException.class)
    public void testErrorOnNonexistentDenominatorFilter() throws IOException {
        parseAssetAllocationsFile(TargetAssetAllocationsTestFiles.NONEXISTENT_DENOMINATOR_FILTER);
    }

    @Test(expected = ValueInstantiationException.class)
    public void testErrorOnFractionGreaterThan1() throws IOException {
        parseAssetAllocationsFile(TargetAssetAllocationsTestFiles.FRACTION_GREATER_THAN_1);
    }

    @Test(expected = ValueInstantiationException.class)
    public void testErrorOnFractionLessThan0() throws IOException {
        parseAssetAllocationsFile(TargetAssetAllocationsTestFiles.FRACTION_LESS_THAN_0);
    }

    private static TargetAssetAllocations parseAssetAllocationsFile(TargetAssetAllocationsTestFiles testFile) throws IOException {
        var mapper = Main.getObjectMapper();
        var assetAllocationsUrl = testFile.getResource();
        return mapper.readValue(assetAllocationsUrl, TargetAssetAllocations.class);
    }
}
