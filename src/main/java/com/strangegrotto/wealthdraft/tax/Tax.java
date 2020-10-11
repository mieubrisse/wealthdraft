package com.strangegrotto.wealthdraft.tax;

public enum Tax {
    NON_PREFERENTIAL_INCOME, // On earned income + non-capgains unearned income + STCG
    SOCIAL_SECURITY,
    MEDICARE,
    NIIT, // "net investment income", aka "Medicare Surtax on Unearned Income"
    SHORT_TERM_CAP_GAINS,
    LONG_TERM_CAP_GAINS,
}
