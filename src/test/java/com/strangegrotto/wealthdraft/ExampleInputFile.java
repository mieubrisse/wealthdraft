package com.strangegrotto.wealthdraft;

import com.strangegrotto.wealthdraft.TestFileProvider;

import java.net.URL;

// TODO Split these up into AssetsFile, GovConstantsFile, etc. like we've done with ProjectionsFile enum
public enum ExampleInputFile implements TestFileProvider {
    SCENARIOS("scenarios-example.yml"),
    GOV_CONSTANTS("gov-constants-example.yml");

    private static final String CONTAINING_DIRNAME = "examples";

    private final String filename;

    ExampleInputFile(String filename) {
        this.filename = filename;
    }

    @Override
    public URL getResource() {
        return getClass().getClassLoader().getResource(CONTAINING_DIRNAME + "/" + this.filename);
    }
}
