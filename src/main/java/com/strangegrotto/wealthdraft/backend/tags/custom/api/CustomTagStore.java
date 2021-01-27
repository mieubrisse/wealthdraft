package com.strangegrotto.wealthdraft.backend.tags.custom.api;

import com.strangegrotto.wealthdraft.backend.tags.custom.api.types.CustomTagDefinition;

import java.util.Map;
import java.util.Optional;

public interface CustomTagStore {
    Map<String, CustomTagDefinition> getTags();

    /**
     * Validates the given tag name-value pair, returning {@link Optional#empty()} if the pair is valid
     * or an optional of the error message if not
     */
    Optional<String> validateTag(String tagName, String tagValue);
}
