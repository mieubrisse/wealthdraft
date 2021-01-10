package com.strangegrotto.wealthdraft;

// TODO Split these up into AssetsFile, GovConstantsFile, etc. like we've done with ProjectionsFile enum
public enum ExampleInputFile implements TestFileProvider {
    SCENARIOS("scenarios.yml"),
    GOV_CONSTANTS("gov-constants.yml");

    private static final String CONTAINING_DIRNAME = "examples";

    private final String filename;

    ExampleInputFile(String filename) {
        this.filename = filename;
    }

    @Override
    public TestResourceDirnames getContainingDirname() {
        return CONTAINING_DIRNAME;
    }

    @Override
    public String getFilename() {
        return this.filename;
    }
}
