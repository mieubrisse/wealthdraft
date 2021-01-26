package com.strangegrotto.wealthdraft.backend.tags.custom.impl;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import com.strangegrotto.wealthdraft.backend.tags.custom.api.types.CustomTagDefinition;
import org.immutables.value.Value;

import java.util.Optional;
import java.util.Set;

@WealthdraftImmutableStyle
@Value.Immutable
@JsonDeserialize(as = ImmSerCustomTagDefinition.class)
interface SerCustomTagDefinition extends CustomTagDefinition {
    @Value.Default
    default boolean isRequired() {
        return false;
    }

    // An empty set means all values are allowed
    Set<String> getAllowedValues();

    Optional<String> getDefaultValue();
}
