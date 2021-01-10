package com.strangegrotto.wealthdraft.assetallocation.datamodel;

import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import com.strangegrotto.wealthdraft.Main;
import com.strangegrotto.wealthdraft.assetallocation.ExpectedExampleTargetAssetAllocations;
import com.strangegrotto.wealthdraft.assetallocation.TargetAssetAllocationsTestFiles;
import com.strangegrotto.wealthdraft.assetfilters.AssetFilter;
import com.strangegrotto.wealthdraft.assetfilters.FiltersTestFiles;
import com.strangegrotto.wealthdraft.assets.definition.AssetDefinitions;
import com.strangegrotto.wealthdraft.assets.definition.AssetDefinitionsTestFiles;
import com.strangegrotto.wealthdraft.assets.definition.ExpectedExampleAssetDefinitions;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TargetAssetAllocationsTest {
    @Test
    public void testValidDeserialization() throws IOException {
        var targetAssetAllocations = parseAssetAllocationsFile(TargetAssetAllocationsTestFiles.EXAMPLE);
        Assert.assertEquals(targetAssetAllocations, ExpectedExampleTargetAssetAllocations.ASSET_ALLOCATIONS);
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
        var assetsUrl = AssetDefinitionsTestFiles.EXAMPLE.getResource();
        var assetDefinitions = mapper.readValue(assetsUrl, AssetDefinitions.class);
        Main.addDeserializersNeedingAssetDefs(mapper, assetDefinitions);

        var filtersUrl = FiltersTestFiles.EXAMPLE.getResource();
        var mapType = mapper.getTypeFactory().constructMapType(HashMap.class, String.class, AssetFilter.class);
        Map<String, AssetFilter> filters = mapper.readValue(filtersUrl, mapType);
        Main.addDeserializersNeedingFilters(mapper, filters);

        var assetAllocationsUrl = testFile.getResource();
        return mapper.readValue(assetAllocationsUrl, TargetAssetAllocations.class);
    }
}
