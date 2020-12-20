package com.strangegrotto.wealthdraft.networth.assets;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import org.immutables.value.Value;

import java.util.Optional;
import java.util.Set;

@WealthdraftImmutableStyle
@Value.Immutable
@JsonDeserialize(as = ImmCustomTagDefinition.class)
public interface CustomTagDefinition {
    // TODO Implement default values

    // An empty set means all values are allowed
    Set<String> getAllowedValues();
}
