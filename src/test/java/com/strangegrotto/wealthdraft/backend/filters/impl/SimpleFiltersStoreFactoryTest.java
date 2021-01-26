package com.strangegrotto.wealthdraft.backend.filters.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.strangegrotto.wealthdraft.Main;
import com.strangegrotto.wealthdraft.backend.filters.FiltersTestFiles;
import com.strangegrotto.wealthdraft.backend.filters.api.FiltersStore;
import com.strangegrotto.wealthdraft.backend.tags.custom.CustomTagsTestFiles;
import com.strangegrotto.wealthdraft.backend.tags.custom.impl.SimpleCustomTagStoreFactoryTest;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class SimpleFiltersStoreFactoryTest {
    @Test
    public void testValidDeserialization() throws IOException {
        var filtersStore = parseFiltersFile(Main.getObjectMapper(), FiltersTestFiles.EXAMPLE);
        Assert.assertEquals(
                ExpectedExampleFilters.EXPECTED_FILTERS,
                filtersStore.getFilters()
        );
    }

    public static FiltersStore parseFiltersFile(ObjectMapper mapper, FiltersTestFiles file) throws IOException {
        // TODO Switch to mocks
        var customTagStore = SimpleCustomTagStoreFactoryTest.parseCustomTagsFile(mapper, CustomTagsTestFiles.EXAMPLE);
        var factory = new SimpleFilterStoreFactory(mapper, customTagStore);
        return factory.create(file.getResource());
    }
}
