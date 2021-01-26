package com.strangegrotto.wealthdraft.backend.assetallocation.impl;

import com.strangegrotto.wealthdraft.Main;
import com.strangegrotto.wealthdraft.backend.assetallocation.ExpectedExampleTargetAssetAllocations;
import com.strangegrotto.wealthdraft.backend.assetallocation.TargetAssetAllocationsTestFiles;
import com.strangegrotto.wealthdraft.backend.filters.impl.SerAssetFilter;
import com.strangegrotto.wealthdraft.backend.filters.FiltersTestFiles;
import com.strangegrotto.wealthdraft.backend.assets.impl.AssetDefinitions;
import com.strangegrotto.wealthdraft.backend.assets.AssetDefinitionsTestFiles;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SerTargetAssetAllocationsTest {
    @Test
    public void testValidDeserialization() throws IOException {
        var targetAssetAllocations = parseAssetAllocationsFile(TargetAssetAllocationsTestFiles.EXAMPLE);
        Assert.assertEquals(targetAssetAllocations, ExpectedExampleTargetAssetAllocations.ASSET_ALLOCATIONS);
    }

    @Test(expected = IllegalStateException.class)
    public void testErrorOnNonexistentNumeratorFilter() throws IOException {
        parseAssetAllocationsFile(TargetAssetAllocationsTestFiles.NONEXISTENT_NUMERATOR_FILTER);
    }

    @Test(expected = IllegalStateException.class)
    public void testErrorOnNonexistentDenominatorFilter() throws IOException {
        parseAssetAllocationsFile(TargetAssetAllocationsTestFiles.NONEXISTENT_DENOMINATOR_FILTER);
    }

    @Test(expected = IllegalStateException.class)
    public void testErrorOnFractionGreaterThan1() throws IOException {
        parseAssetAllocationsFile(TargetAssetAllocationsTestFiles.FRACTION_GREATER_THAN_1);
    }

    @Test(expected = IllegalStateException.class)
    public void testErrorOnFractionLessThan0() throws IOException {
        parseAssetAllocationsFile(TargetAssetAllocationsTestFiles.FRACTION_LESS_THAN_0);
    }

    public static SerTargetAssetAllocations parseAssetAllocationsFile(TargetAssetAllocationsTestFiles testFile) throws IOException {
        var mapper = Main.getObjectMapper();
        var assetsUrl = AssetDefinitionsTestFiles.EXAMPLE.getResource();
        var assetDefinitions = mapper.readValue(assetsUrl, AssetDefinitions.class);
        Main.addDeserializersNeedingAssetDefs(mapper, assetDefinitions);

        var filtersUrl = FiltersTestFiles.EXAMPLE.getResource();
        var mapType = mapper.getTypeFactory().constructMapType(HashMap.class, String.class, SerAssetFilter.class);
        Map<String, SerAssetFilter> filters = mapper.readValue(filtersUrl, mapType);
        Main.addDeserializersNeedingFilters(mapper, filters);

        var assetAllocationsUrl = testFile.getResource();
        return mapper.readValue(assetAllocationsUrl, SerTargetAssetAllocations.class);
    }
}
