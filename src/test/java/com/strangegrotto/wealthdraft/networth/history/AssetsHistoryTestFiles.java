package com.strangegrotto.wealthdraft.networth.history;

import com.strangegrotto.wealthdraft.TestFileProvider;
import com.strangegrotto.wealthdraft.TestResourceDirnames;

public enum AssetsHistoryTestFiles implements TestFileProvider {
    NONEXISTENT_ASSET("nonexistent-asset.yml"),
    FUTURE_DATE("future-date.yml"),
    EXAMPLE(TestResourceDirnames.EXAMPLES.getDirname(), "assets-history.yml");

    private final String containingDirname;
    private final String filename;

    AssetsHistoryTestFiles(String containingDirname, String filename) {
        this.containingDirname = containingDirname;
        this.filename = filename;
    }

    AssetsHistoryTestFiles(String filename) {
        this(TestResourceDirnames.ASSETS_HISTORY_DESERIALIZATION_TESTS.getDirname(), filename);
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
