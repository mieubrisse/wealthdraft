package com.strangegrotto.wealthdraft;

public enum TestResourceDirnames {
    EXAMPLES("examples"),
    MAIN_TESTS("main"),
    ASSET_ALLOCATION_DESERIALIZATION_TESTS("asset-allocation-deserialization"),
    ASSET_DEFINITIONS_DESERIALIZATION_TESTS("assets-deserialization"),
    ASSET_FILTERS_DESERIALIZATION_TESTS("asset-filters"),
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
