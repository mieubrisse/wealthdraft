package com.strangegrotto.wealthdraft.govconstants;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableForeignIncomeConstants.class)
public interface ForeignIncomeConstants {
    long getForeignEarnedIncomeExemption();

    @Value.Check
    default void check() {
        Preconditions.checkState(getForeignEarnedIncomeExemption() > 0, "Foreign earned income exemption must be > 0");
    }
}
