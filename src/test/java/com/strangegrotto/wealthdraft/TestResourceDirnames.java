package com.strangegrotto.wealthdraft;

public enum TestResourceDirnames {
    EXAMPLES("examples"),
    ASSET_DESERIALIZATION_TESTS("assets-deserialization"),
    PROJECTIONS_DESERIALIZATION_TESTS("projections-deserialization");

    private final String dirname;

    TestResourceDirnames(String dirname) {
        this.dirname = dirname;
    }

    public String getDirname() {
        return dirname;
    }
}
