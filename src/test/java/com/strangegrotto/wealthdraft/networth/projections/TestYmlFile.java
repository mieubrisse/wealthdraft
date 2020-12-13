package com.strangegrotto.wealthdraft.networth.projections;

public enum TestYmlFile {
    SCENARIOS("scenarios-example.yml"),
    GOV_CONSTANTS("gov-constants-example.yml"),
    ASSETS("assets-example.yml"),
    PROJECTIONS("projections-example.yml");

    private final String filename;

    TestYmlFile(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }
}
