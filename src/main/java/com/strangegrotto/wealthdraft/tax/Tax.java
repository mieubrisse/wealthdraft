package com.strangegrotto.wealthdraft.tax;

import org.slf4j.Logger;

public enum Tax {
    // On earned income + non-capgains unearned income + STCG
    FED_NON_PREF_INCOME("Fed Non-Preferential Income Tax"),

    // longterm cap gains and qualified dividends
    FED_PREF_INCOME("Fed Preferential Income Tax"),

    SOCIAL_SECURITY("Social Security Tax"),

    MEDICARE("Medicare Tax"),

    // "net investment income", aka "Medicare Surtax on Unearned Income"
    NIIT("Net Investment Income Tax");

    private final String prettyName;
    private Tax(String prettyName) {
        this.prettyName = prettyName;
    }

    public String getPrettyName() { return this.prettyName; }
}
