package com.strangegrotto.wealthdraft.assetallocation;

import com.strangegrotto.wealthdraft.TestFileProvider;
import com.strangegrotto.wealthdraft.TestResourceDirnames;

public enum TargetAssetAllocationsTestFiles implements TestFileProvider {
    NONEXISTENT_NUMERATOR_FILTER("nonexistent-numerator-filter.yml"),
    NONEXISTENT_DENOMINATOR_FILTER("nonexistent-denominator-filter.yml"),
    FRACTION_GREATER_THAN_1("fraction-greater-than-1.yml"),
    FRACTION_LESS_THAN_0("fraction-less-than-0.yml"),
    EXAMPLE(TestResourceDirnames.EXAMPLES, "asset-allocations.yml");

    private final TestResourceDirnames containingDirname;
    private final String filename;

    TargetAssetAllocationsTestFiles(TestResourceDirnames containingDirname, String filename) {
        this.containingDirname = containingDirname;
        this.filename = filename;
    }

    TargetAssetAllocationsTestFiles(String filename) {
        this(TestResourceDirnames.ASSET_ALLOCATION_DESERIALIZATION_TESTS, filename);
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
