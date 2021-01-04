package com.strangegrotto.wealthdraft.assetallocation;

import com.strangegrotto.wealthdraft.TestFileProvider;
import com.strangegrotto.wealthdraft.TestResourceDirnames;

public enum TargetAssetAllocationsTestFiles implements TestFileProvider {
    EXAMPLE(TestResourceDirnames.EXAMPLES.getDirname(), "asset-allocations.yml");

    private final String containingDirname;
    private final String filename;

    TargetAssetAllocationsTestFiles(String containingDirname, String filename) {
        this.containingDirname = containingDirname;
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
