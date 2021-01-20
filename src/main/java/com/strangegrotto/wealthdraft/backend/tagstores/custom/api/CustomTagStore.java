package com.strangegrotto.wealthdraft.backend.tagstores.custom.api;

import com.strangegrotto.wealthdraft.backend.tagstores.custom.api.types.CustomTagDefinition;

import java.util.Map;

public interface CustomTagStore {
    Map<String, CustomTagDefinition> getTags();
}
