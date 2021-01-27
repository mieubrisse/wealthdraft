package com.strangegrotto.wealthdraft.backend.assethistory;

import com.strangegrotto.wealthdraft.TestFileProvider;
import com.strangegrotto.wealthdraft.TestResourceDirnames;

public enum AssetsHistoryTestFiles implements TestFileProvider {
    NONEXISTENT_ASSET("nonexistent-asset.yml"),
    FUTURE_DATE("future-date.yml"),
    EXAMPLE(TestResourceDirnames.EXAMPLES, "assets-history.yml");

    private final TestResourceDirnames containingDirname;
    private final String filename;

    AssetsHistoryTestFiles(TestResourceDirnames containingDirname, String filename) {
        this.containingDirname = containingDirname;
        this.filename = filename;
    }

    AssetsHistoryTestFiles(String filename) {
        this(TestResourceDirnames.ASSETS_HISTORY_DESERIALIZATION_TESTS, filename);
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
