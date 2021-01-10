package com.strangegrotto.wealthdraft;

public enum MainTestFiles implements TestFileProvider {
    DUPLICATE_KEYS("duplicate-keys.yml");

    private final TestResourceDirnames containingDirname;
    private final String filename;

    MainTestFiles(TestResourceDirnames containingDirname, String filename) {
        this.containingDirname = containingDirname;
        this.filename = filename;
    }

    MainTestFiles(String filename) {
        this(TestResourceDirnames.MAIN_TESTS, filename);
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
