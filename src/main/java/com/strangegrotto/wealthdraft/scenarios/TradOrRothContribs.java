package com.strangegrotto.wealthdraft.scenarios;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import org.immutables.value.Value;

/**
 * Represents classification of IRA or 401k contributions
 * The percentages are percentages OF THE YEAR'S TOTAL LIMIT, and should add up to 1.0!
 */
@Value.Immutable
@JsonDeserialize(as = ImmutableTradOrRothContribs.class)
public interface TradOrRothContribs {

    long getTrad();
    long getRoth();

    @Value.Check
    default void check() {
        Preconditions.checkState(getTrad() >= 0, "traditional IRA contrib must be >= 0");
        Preconditions.checkState(getRoth() >= 0, "Roth IRA contrib must be >= 0");
    }
}

