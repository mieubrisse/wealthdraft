package com.strangegrotto.wealthdraft.networth.projections;

import com.strangegrotto.wealthdraft.errors.ValOrGerr;
import org.immutables.value.Value;

import java.math.BigDecimal;
import java.util.Map;

@Value.Immutable
public interface Projections {
    BigDecimal getDefaultAnnualGrowth();

    Map<String, ValOrGerr<ProjectionScenario>> getScenarios();
}
