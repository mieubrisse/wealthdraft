package com.strangegrotto.wealthdraft;

import org.immutables.value.Value;

@Value.Immutable
public interface IncomeStreams {
    int getEarnedIncome();

    int getOtherUnearnedIncome();

    int getShortTermCapGains();

    int getLongTermCapGains();
}
