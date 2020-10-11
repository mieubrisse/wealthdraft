package com.strangegrotto.wealthdraft.tax;

import org.immutables.value.Value;

@Value.Immutable
public interface Deductions {
    long getTrad401kDeduction();

    long getTradIraDeduction();

    long getStandardDeduction();
}
