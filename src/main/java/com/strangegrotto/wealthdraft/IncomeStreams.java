package com.strangegrotto.wealthdraft;

import org.immutables.value.Value;

@Value.Immutable
public interface IncomeStreams {

    long getEarnedIncome();
    long getOtherUnearnedIncome();
    long getShortTermCapGains();
    long getLongTermCapGains();

}
