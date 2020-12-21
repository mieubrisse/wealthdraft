package com.strangegrotto.wealthdraft;

public enum TestResourceDirnames {
    EXAMPLES("examples"),
    ASSET_DEFINITIONS_DESERIALIZATION_TESTS("assets-deserialization"),
    ASSETS_HISTORY_DESERIALIZATION_TESTS("assets-history-deserialization"),
    PROJECTIONS_DESERIALIZATION_TESTS("projections-deserialization");

    private final String dirname;

    TestResourceDirnames(String dirname) {
        this.dirname = dirname;
    }

    public String getDirname() {
        return dirname;
    }
}
