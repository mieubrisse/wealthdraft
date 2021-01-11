package com.strangegrotto.wealthdraft.tagstores.custom.impl;

import com.strangegrotto.wealthdraft.tagstores.custom.api.CustomTagStore;
import com.strangegrotto.wealthdraft.tagstores.custom.api.types.CustomTagDefinition;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SimpleCustomTagStore implements CustomTagStore {
    Map<String, CustomTagDefinition> customTags;

    public SimpleCustomTagStore(Map<String, ? extends CustomTagDefinition> customTags) {
        this.customTags = Map.copyOf(customTags);
    }

    @Override
    public Map<String, CustomTagDefinition> getTags() {
        return this.customTags;
    }
}
