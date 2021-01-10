package com.strangegrotto.wealthdraft.assets.definition;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import org.immutables.value.Value;

import java.util.List;
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

    @Value.Check
    default void check() {
        if (getDefaultValue().isPresent() && getAllowedValues().size() > 0) {
            var defaultValue = getDefaultValue().get();
            var allowedValuesStr = String.join(", ", getAllowedValues());
            Preconditions.checkState(
                    getAllowedValues().contains(defaultValue),
                    "Default value '%s' isn't contained in allowed values '%s'",
                    defaultValue,
                    allowedValuesStr
            );
        }
    }
}
