package com.strangegrotto.wealthdraft.networth.history;

import com.strangegrotto.wealthdraft.TestFileProvider;
import com.strangegrotto.wealthdraft.TestResourceDirnames;

import java.net.URL;

public enum AssetsFiles implements TestFileProvider {
    NONEXISTENT_ASSET("nonexistent-asset.yml"),
    FUTURE_DATE("future-date.yml"),
    DISALLOWED_TAG_VALUE("disallowed-tag-value.yml"),
    EXAMPLE(TestResourceDirnames.EXAMPLES.getDirname(), "assets-example.yml");

    private final String containingDirname;
    private final String filename;

    AssetsFiles(String containingDirname, String filename) {
        this.containingDirname = containingDirname;
        this.filename = filename;
    }

    AssetsFiles(String filename) {
        this(TestResourceDirnames.ASSET_DESERIALIZATION_TESTS.getDirname(), filename);
    }

    @Override
    public URL getResource() {
        return getClass().getClassLoader().getResource(this.containingDirname + "/" + this.filename);
    }
}
