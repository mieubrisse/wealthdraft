package com.strangegrotto.wealthdraft.backend.tags.custom;

import com.strangegrotto.wealthdraft.TestFileProvider;
import com.strangegrotto.wealthdraft.TestResourceDirnames;

public enum CustomTagsTestFiles implements TestFileProvider {
    INTRINSIC_TAG_COLLISION("intrinsic-tag-collision.yml"),
    UNRECOGNIZED_DEFAULT_VALUE("unrecognized-default-value.yml"),
    EXAMPLE(TestResourceDirnames.EXAMPLES, "tags.yml");

    private final TestResourceDirnames containingDirname;
    private final String filename;

    CustomTagsTestFiles(TestResourceDirnames containingDirname, String filename) {
        this.containingDirname = containingDirname;
        this.filename = filename;
    }

    CustomTagsTestFiles(String filename) {
        this(TestResourceDirnames.TAGS_TESTS, filename);
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
