package com.strangegrotto.wealthdraft.scenarios;

import org.immutables.value.Value;

@Value.Immutable
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
