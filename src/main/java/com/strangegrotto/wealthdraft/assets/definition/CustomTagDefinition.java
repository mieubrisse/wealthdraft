package com.strangegrotto.wealthdraft.assets.definition;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import org.immutables.value.Value;

import java.util.Optional;
import java.util.Set;

@WealthdraftImmutableStyle
@Value.Immutable
@JsonDeserialize(as = ImmCustomTagDefinition.class)
public interface CustomTagDefinition {
    @Value.Default
    default boolean isRequired() {
        return false;
    }

    // An empty set means all values are allowed
    Set<String> getAllowedValues();

    Optional<String> getDefaultValue();
}
