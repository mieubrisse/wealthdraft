package com.strangegrotto.wealthdraft.networth.projections;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.strangegrotto.wealthdraft.networth.projections.raw.RawAssetChange;
import org.immutables.value.Value;

import java.util.Map;

@Value.Immutable
@JsonDeserialize(as = ProjectionScenario.class)
public interface ProjectionScenario {
    String getName();

    // Mapping of change_date -> (asset_id -> asset_change)
    Map<String, Map<String, RawAssetChange>> getChanges();
}
