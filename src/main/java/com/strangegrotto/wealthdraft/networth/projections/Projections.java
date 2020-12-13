package com.strangegrotto.wealthdraft.networth.projections;

import com.strangegrotto.wealthdraft.errors.ValOrGerr;
import org.immutables.value.Value;

import java.util.Map;

@Value.Immutable
public interface Projections {
    Double getDefaultAnnualGrowth();

    Map<String, ValOrGerr<ProjectionScenario>> getScenarios();
}
