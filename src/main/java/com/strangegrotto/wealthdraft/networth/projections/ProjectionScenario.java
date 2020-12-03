package com.strangegrotto.wealthdraft.networth.projections;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.util.Map;

@Value.Immutable
@JsonDeserialize(as = ImmutableProjectionScenario.class)
public interface ProjectionScenario {
    String getName();

    // The ID of a scenario to base the scenario on
    String getBase();

    // Mapping of change_date -> (asset_id -> asset_change)
    Map<String, Map<String, AssetChange>> getChanges();
}
