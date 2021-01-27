package com.strangegrotto.wealthdraft.backend.assetallocation.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.strangegrotto.wealthdraft.Main;
import com.strangegrotto.wealthdraft.backend.assetallocation.TargetAssetAllocationsTestFiles;
import com.strangegrotto.wealthdraft.backend.assetallocation.api.TargetAssetAllocationsStore;
import com.strangegrotto.wealthdraft.backend.filters.FiltersTestFiles;
import com.strangegrotto.wealthdraft.backend.filters.impl.SimpleFiltersStoreFactoryTest;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class SimpleTargetAssetAllocationsStoreFactoryTest {
    @Test
    public void testValidDeserialization() throws IOException {
        var targetAssetAllocationsStore = parseAssetAllocationsFile(
                Main.getObjectMapper(),
                TargetAssetAllocationsTestFiles.EXAMPLE
        );
        var targetAllocations = targetAssetAllocationsStore.getTargetAllocations();
        Assert.assertEquals(targetAllocations, ExpectedExampleTargetAssetAllocations.TARGET_ALLOCATIONS);
    }

    @Test(expected = IllegalStateException.class)
    public void testErrorOnNonexistentNumeratorFilter() throws IOException {
        parseAssetAllocationsFile(Main.getObjectMapper(), TargetAssetAllocationsTestFiles.NONEXISTENT_NUMERATOR_FILTER);
    }

    @Test(expected = IllegalStateException.class)
    public void testErrorOnNonexistentDenominatorFilter() throws IOException {
        parseAssetAllocationsFile(Main.getObjectMapper(), TargetAssetAllocationsTestFiles.NONEXISTENT_DENOMINATOR_FILTER);
    }

    @Test(expected = IllegalStateException.class)
    public void testErrorOnFractionGreaterThan1() throws IOException {
        parseAssetAllocationsFile(Main.getObjectMapper(), TargetAssetAllocationsTestFiles.FRACTION_GREATER_THAN_1);
    }

    @Test(expected = IllegalStateException.class)
    public void testErrorOnFractionLessThan0() throws IOException {
        parseAssetAllocationsFile(Main.getObjectMapper(), TargetAssetAllocationsTestFiles.FRACTION_LESS_THAN_0);
    }

    public static TargetAssetAllocationsStore parseAssetAllocationsFile(
            ObjectMapper mapper,
            TargetAssetAllocationsTestFiles testFile
    ) throws IOException {
        // TODO use mocks
        var filtersStore = SimpleFiltersStoreFactoryTest.parseFiltersFile(mapper, FiltersTestFiles.EXAMPLE);
        var factory = new SimpleTargetAssetAllocationsStoreFactory(mapper, filtersStore);
        return factory.create(testFile.getResource());
    }
}
