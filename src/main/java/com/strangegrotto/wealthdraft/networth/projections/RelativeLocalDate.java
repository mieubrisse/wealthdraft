package com.strangegrotto.wealthdraft.networth.projections;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.time.LocalDate;

/**
 * Represents a LocalDate that's specified using relative syntax
 */
@Value.Immutable
@JsonDeserialize(as = ImmutableRelativeLocalDate.class, using = RelativeLocalDateDeserializer.class)
public interface RelativeLocalDate {
    LocalDate getLocalDate();
}
