package com.strangegrotto.wealthdraft.scenarios;

import org.immutables.value.Value;

@Value.Immutable
public interface IncomeStreams {

    // NOTE NOTE NOTE: When adding a new income stream, make sure to update the 'getTotal' method!
    long getEarnedIncome();
    long getShortTermCapGains();
    long getLongTermCapGains();
    long getOrdinaryDividends();
    long getQualifiedDividends();
    long getOtherUnearnedIncome();

    @Value.Derived
    default long getPreferentialUnearnedIncome() {
        return getLongTermCapGains() + getQualifiedDividends();
    }

    @Value.Derived
    default long getNonPreferentialUnearnedIncome() {
        return getShortTermCapGains() + getOrdinaryDividends() + getOtherUnearnedIncome();
    }

    @Value.Derived
    default long getTotal() {
        return getEarnedIncome() +
                getNonPreferentialUnearnedIncome() +
                getPreferentialUnearnedIncome();
    }
}
