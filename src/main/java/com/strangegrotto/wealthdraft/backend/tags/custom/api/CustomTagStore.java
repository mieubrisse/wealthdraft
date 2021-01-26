package com.strangegrotto.wealthdraft.backend.tags.custom.api;

import com.strangegrotto.wealthdraft.backend.tags.custom.api.types.CustomTagDefinition;

import java.util.Map;

public interface CustomTagStore {
    Map<String, CustomTagDefinition> getTags();
}
