package com.strangegrotto.wealthdraft.networth.projections;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.util.Map;

@Value.Immutable
@JsonDeserialize(as = ImmutableProjections.class)
public interface Projections {
    Double getDefaultAnnualGrowth();

    Map<String, ProjectionScenario> getScenarios();
}
