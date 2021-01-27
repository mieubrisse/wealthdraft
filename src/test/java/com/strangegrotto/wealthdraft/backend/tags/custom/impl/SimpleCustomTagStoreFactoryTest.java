package com.strangegrotto.wealthdraft.backend.tags.custom.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.strangegrotto.wealthdraft.Main;
import com.strangegrotto.wealthdraft.backend.tags.custom.CustomTagsTestFiles;
import com.strangegrotto.wealthdraft.backend.tags.custom.api.CustomTagStore;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class SimpleCustomTagStoreFactoryTest {
    @Test
    public void testValidDeserialization() throws IOException {
        var customTagStore = parseCustomTagsFile(Main.getObjectMapper(), CustomTagsTestFiles.EXAMPLE);
        Assert.assertEquals(
                ExpectedExampleCustomTags.EXPECTED_CUSTOM_TAGS,
                customTagStore.getTags()
        );
    }

    @Test(expected = IllegalStateException.class)
    public void testErrorOnIntrinsicTagCollision() throws IOException {
        parseCustomTagsFile(Main.getObjectMapper(), CustomTagsTestFiles.INTRINSIC_TAG_COLLISION);
    }

    @Test(expected = IllegalStateException.class)
    public void testErrorOnUnrecognizedDefaultValue() throws IOException {
        parseCustomTagsFile(Main.getObjectMapper(), CustomTagsTestFiles.UNRECOGNIZED_DEFAULT_VALUE);
    }

    public static CustomTagStore parseCustomTagsFile(ObjectMapper baseMapper, CustomTagsTestFiles file) throws IOException {
        var factory = new SimpleCustomTagStoreFactory(baseMapper);
        return factory.create(file.getResource());
    }
}
