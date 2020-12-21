package com.strangegrotto.wealthdraft.assets.definition;

import com.strangegrotto.wealthdraft.TestFileProvider;
import com.strangegrotto.wealthdraft.TestResourceDirnames;

public enum AssetDefinitionsTestFiles implements TestFileProvider  {
    DISALLOWED_TAG_VALUES("disallowed-tag-value.yml"),
    INTRINSIC_TAG_COLLISION("intrinsic-tag-collision.yml"),
    UNRECOGNIZED_TAG("unrecognized-tag.yml"),
    EVERY_ASSET_TYPE("every-asset-type.yml"),
    EXAMPLE(TestResourceDirnames.EXAMPLES.getDirname(), "assets.yml");

    private final String containingDirname;
    private final String filename;

    AssetDefinitionsTestFiles(String containingDirname, String filename) {
        this.containingDirname = containingDirname;
        this.filename = filename;
    }

    AssetDefinitionsTestFiles(String filename) {
        this(
                TestResourceDirnames.ASSET_DEFINITIONS_DESERIALIZATION_TESTS.getDirname(),
                filename
        );
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
