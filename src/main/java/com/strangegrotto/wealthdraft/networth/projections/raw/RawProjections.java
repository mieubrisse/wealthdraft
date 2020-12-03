package com.strangegrotto.wealthdraft.networth.projections.raw;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.strangegrotto.wealthdraft.networth.projections.ImmutableProjections;
import com.strangegrotto.wealthdraft.networth.projections.ProjectionScenario;
import org.immutables.value.Value;

import java.util.Map;

@Value.Immutable
@JsonDeserialize(as = ImmutableRawProjections.class)
public interface RawProjections {
    Double getDefaultGrowth();

    Map<String, ProjectionScenario> getScenarios();
}
