package com.strangegrotto.wealthdraft.assets.impl;

import com.strangegrotto.wealthdraft.TestFileProvider;
import com.strangegrotto.wealthdraft.TestResourceDirnames;

public enum AssetDefinitionsTestFiles implements TestFileProvider  {
    DISALLOWED_TAG_VALUES("disallowed-tag-value.yml"),
    INTRINSIC_TAG_COLLISION("intrinsic-tag-collision.yml"),
    UNRECOGNIZED_TAG("unrecognized-tag.yml"),
    EVERY_ASSET_TYPE("every-asset-type.yml"),
    UNSPECIFIED_TAGS_IS_EMPTY_MAP("unspecified-tags-is-empty-map.yml"),
    MISSING_REQUIRED_TAG("missing-required-tag.yml"),
    TEST_DEFAULT_TAGS("test-default-tags.yml"),
    EXAMPLE(TestResourceDirnames.EXAMPLES, "assets.yml");

    private final TestResourceDirnames containingDirname;
    private final String filename;

    AssetDefinitionsTestFiles(TestResourceDirnames containingDirname, String filename) {
        this.containingDirname = containingDirname;
        this.filename = filename;
    }

    AssetDefinitionsTestFiles(String filename) {
        this(
                TestResourceDirnames.ASSET_DEFINITIONS_DESERIALIZATION_TESTS,
                filename
        );
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
