package com.strangegrotto.wealthdraft.backend.tags.custom.api.types;

import java.util.Optional;
import java.util.Set;

public class MockCustomTagDefinition implements CustomTagDefinition {
    private final boolean isRequired;
    private final Set<String> allowedValues;
    private final Optional<String> defaultValue;

    public MockCustomTagDefinition(boolean isRequired, Set<String> allowedValues, Optional<String> defaultValue) {
        this.isRequired = isRequired;
        this.allowedValues = allowedValues;
        this.defaultValue = defaultValue;
    }

    @Override
    public boolean isRequired() {
        return this.isRequired;
    }

    @Override
    public Set<String> getAllowedValues() {
        return this.allowedValues;
    }

    @Override
    public Optional<String> getDefaultValue() {
        return this.defaultValue;
    }
}
