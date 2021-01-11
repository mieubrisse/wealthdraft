package com.strangegrotto.wealthdraft.tagstores.custom.api.types;

import java.util.Optional;
import java.util.Set;

public interface CustomTagDefinition {
    boolean isRequired();

    // An empty set means all values are allowed
    Set<String> getAllowedValues();

    Optional<String> getDefaultValue();
}
