package com.strangegrotto.wealthdraft.assets.definition;

import com.strangegrotto.wealthdraft.TestFileProvider;
import com.strangegrotto.wealthdraft.TestResourceDirnames;

import java.net.URL;

public enum AssetDefinitionsFiles implements TestFileProvider  {
    DISALLOWED_TAG_VALUES("disallowed-tag-value.yml"),
    EXAMPLE(TestResourceDirnames.EXAMPLES.getDirname(), "assets-example.yml");

    private final String containingDirname;
    private final String filename;

    AssetDefinitionsFiles(String containingDirname, String filename) {
        this.containingDirname = containingDirname;
        this.filename = filename;
    }

    AssetDefinitionsFiles(String filename) {
        this(
                TestResourceDirnames.ASSET_DEFINITIONS_DESERIALIZATION_TESTS.getDirname(),
                filename
        );
    }

    @Override
    public URL getResource() {
        return getClass().getClassLoader().getResource(this.containingDirname + "/" + this.filename);
    }
}
