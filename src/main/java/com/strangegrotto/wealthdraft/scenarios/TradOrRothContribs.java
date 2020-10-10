package com.strangegrotto.wealthdraft.scenarios;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

/**
 * Represents classification of IRA or 401k contributions
 * The percentages are percentages OF THE YEAR'S TOTAL LIMIT, and should add up to 1.0!
 */
@Value.Immutable
@JsonDeserialize(as = ImmutableTradOrRothContribs.class)
public interface TradOrRothContribs {
    Double getTrad();
    Double getRoth();
}
