package com.strangegrotto.wealthdraft.backend.tags.custom.impl;

import com.strangegrotto.wealthdraft.backend.tags.custom.api.CustomTagStore;
import com.strangegrotto.wealthdraft.backend.tags.custom.api.types.CustomTagDefinition;

import java.util.Map;
import java.util.Optional;

class SimpleCustomTagStore implements CustomTagStore {
    Map<String, CustomTagDefinition> customTags;

    public SimpleCustomTagStore(Map<String, ? extends CustomTagDefinition> customTags) {
        this.customTags = Map.copyOf(customTags);
    }

    @Override
    public Map<String, CustomTagDefinition> getTags() {
        return this.customTags;
    }

    @Override
    public Optional<String> validateTag(String tagName, String tagValue) {
        return Optional.empty();
    }
}
