package com.strangegrotto.wealthdraft.assetallocation;

import com.strangegrotto.wealthdraft.TestFileProvider;
import com.strangegrotto.wealthdraft.TestResourceDirnames;

public enum TargetAssetAllocationsTestFiles implements TestFileProvider {
    ALL_SET_MATH_OPERATORS("all-set-math-operators.yml"),
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
