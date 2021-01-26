package com.strangegrotto.wealthdraft.backend.tagstores.custom.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.strangegrotto.wealthdraft.Main;
import com.strangegrotto.wealthdraft.backend.tagstores.custom.CustomTagsTestFiles;
import com.strangegrotto.wealthdraft.backend.tagstores.custom.api.CustomTagStore;
import com.strangegrotto.wealthdraft.backend.tagstores.intrinsic.impl.SimpleIntrinsicTagStore;
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
        var intrinsicTagStore = new SimpleIntrinsicTagStore();
        var factory = new SimpleCustomTagStoreFactory(baseMapper, intrinsicTagStore);
        return factory.create(file.getResource());
    }
}
