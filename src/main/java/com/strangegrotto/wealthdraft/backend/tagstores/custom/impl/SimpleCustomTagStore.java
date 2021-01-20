package com.strangegrotto.wealthdraft.backend.tagstores.custom.impl;

import com.strangegrotto.wealthdraft.backend.tagstores.custom.api.CustomTagStore;
import com.strangegrotto.wealthdraft.backend.tagstores.custom.api.types.CustomTagDefinition;

import java.util.Map;

class SimpleCustomTagStore implements CustomTagStore {
    Map<String, CustomTagDefinition> customTags;

    public SimpleCustomTagStore(Map<String, ? extends CustomTagDefinition> customTags) {
        this.customTags = Map.copyOf(customTags);
    }

    @Override
    public Map<String, CustomTagDefinition> getTags() {
        return this.customTags;
    }
}
