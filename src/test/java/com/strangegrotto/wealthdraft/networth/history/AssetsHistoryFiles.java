package com.strangegrotto.wealthdraft.networth.history;

import com.strangegrotto.wealthdraft.TestFileProvider;
import com.strangegrotto.wealthdraft.TestResourceDirnames;

import java.net.URL;

public enum AssetsHistoryFiles implements TestFileProvider {
    NONEXISTENT_ASSET("nonexistent-asset.yml"),
    FUTURE_DATE("future-date.yml"),
    EXAMPLE(TestResourceDirnames.EXAMPLES.getDirname(), "assets-history-example.yml");

    private final String containingDirname;
    private final String filename;

    AssetsHistoryFiles(String containingDirname, String filename) {
        this.containingDirname = containingDirname;
        this.filename = filename;
    }

    AssetsHistoryFiles(String filename) {
        this(TestResourceDirnames.ASSETS_HISTORY_DESERIALIZATION_TESTS.getDirname(), filename);
    }

    @Override
    public URL getResource() {
        return getClass().getClassLoader().getResource(this.containingDirname + "/" + this.filename);
    }
}
