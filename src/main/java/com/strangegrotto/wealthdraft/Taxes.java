package com.strangegrotto.wealthdraft;

import org.immutables.value.Value;

@Value.Immutable
public interface Taxes {
    int getFederalIncome();
    int getSocialSecurity();
    int getMedicare();
    int getNetInvestmentIncome();
    int getShortTermCapGains();
    int getLongTermCapGains();
}
