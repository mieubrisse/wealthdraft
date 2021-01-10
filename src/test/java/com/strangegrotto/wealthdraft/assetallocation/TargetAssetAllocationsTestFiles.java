package com.strangegrotto.wealthdraft.assetallocation;

import com.strangegrotto.wealthdraft.TestFileProvider;
import com.strangegrotto.wealthdraft.TestResourceDirnames;

public enum TargetAssetAllocationsTestFiles implements TestFileProvider {
    ALL_SET_MATH_OPERATORS("all-set-math-operators.yml"),
    NONEXISTENT_NUMERATOR_FILTER("nonexistent-numerator-filter.yml"),
    NONEXISTENT_DENOMINATOR_FILTER("nonexistent-denominator-filter.yml"),
    FRACTION_GREATER_THAN_1("fraction-greater-than-1.yml"),
    FRACTION_LESS_THAN_0("fraction-less-than-0.yml"),
    FILTER_CYCLE("filter-cycle.yml"),
    NONEXISTENT_EMBEDDED_FILTER("nonexistent-embedded-filter.yml"),
    EXAMPLE(TestResourceDirnames.EXAMPLES.getDirname(), "asset-allocations.yml");

    private final String containingDirname;
    private final String filename;

    TargetAssetAllocationsTestFiles(String containingDirname, String filename) {
        this.containingDirname = containingDirname;
        this.filename = filename;
    }

    TargetAssetAllocationsTestFiles(String filename) {
        this.containingDirname = TestResourceDirnames.ASSET_ALLOCATION_DESERIALIZATION_TESTS.getDirname();
        this.filename = filename;
    }

    @Override
    public String getContainingDirname() {
        return this.containingDirname;
    }

    @Override
    public String getFilename() {
        return this.filename;
    }
}
