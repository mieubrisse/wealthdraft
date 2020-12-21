package com.strangegrotto.wealthdraft;

import java.net.URL;

public enum MainTestFiles implements TestFileProvider {
    DUPLICATE_KEYS("duplicate-keys.yml");

    private final String containingDirname;
    private final String filename;

    MainTestFiles(String containingDirname, String filename) {
        this.containingDirname = containingDirname;
        this.filename = filename;
    }

    MainTestFiles(String filename) {
        this(TestResourceDirnames.MAIN_TESTS.getDirname(), filename);
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
