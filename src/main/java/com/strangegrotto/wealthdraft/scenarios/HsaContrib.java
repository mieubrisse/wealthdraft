package com.strangegrotto.wealthdraft.scenarios;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import org.immutables.value.Value;

/**
 * Characterizes the components of HSA contribution
 */
@Value.Immutable
@JsonDeserialize(as = ImmutableHsaContrib.class)
public interface HsaContrib {
    // The distinction between these two is important because HSA contributions that are made through payroll
    // aren't subject to FICA taxes (because they're excluded from your gross income entirely), while
    // other contributions are
    long getViaPayroll();
    long getViaOtherMethods();

    @Value.Check
    default void check() {
        Preconditions.checkState(
                getViaPayroll() >= 0,
                "HSA contributions made via payroll must be >= 0e"
        );
        Preconditions.checkState(
                getViaOtherMethods() >= 0,
                "HSA contributions made via other methods must be >= 0"
        );
    }
}
