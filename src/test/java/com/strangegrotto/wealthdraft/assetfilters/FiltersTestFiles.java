package com.strangegrotto.wealthdraft.assetfilters;

import com.strangegrotto.wealthdraft.TestFileProvider;
import com.strangegrotto.wealthdraft.TestResourceDirnames;

public enum FiltersTestFiles implements TestFileProvider {
    EXAMPLE(TestResourceDirnames.EXAMPLES, "filters.yml");

    private final TestResourceDirnames containingDirname;
    private final String filename;

    FiltersTestFiles(TestResourceDirnames containingDirname, String filename) {
        this.containingDirname = containingDirname;
        this.filename = filename;
    }

    FiltersTestFiles(String filename) {
        this(TestResourceDirnames.ASSET_FILTERS_DESERIALIZATION_TESTS, filename);
    }

    @Override
    public TestResourceDirnames getContainingDirname() {
        return this.containingDirname;
    }

    @Override
    public String getFilename() {
        return this.filename;
    }
}
