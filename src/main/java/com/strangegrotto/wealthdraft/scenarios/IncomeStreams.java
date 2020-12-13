package com.strangegrotto.wealthdraft.scenarios;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableIncomeStreams.class)
public interface IncomeStreams {

    // NOTE NOTE NOTE: When adding a new income stream, make sure to update the 'getTotal' method!
    long getEarnedIncome();
    long getNonPreferentialUnearnedIncome();
    long getPreferentialUnearnedIncome();

    @Value.Derived
    default long getTotal() {
        return getEarnedIncome() +
                getNonPreferentialUnearnedIncome() +
                getPreferentialUnearnedIncome();
    }
}
