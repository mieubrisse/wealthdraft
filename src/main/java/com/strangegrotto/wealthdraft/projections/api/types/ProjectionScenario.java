package com.strangegrotto.wealthdraft.projections.api.types;

import com.strangegrotto.wealthdraft.projections.impl.temporal.AssetChange;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;

public interface ProjectionScenario {
    String getName();

    Optional<String> getBase();

    SortedMap<LocalDate, Map<String, AssetChange>> getAssetChanges();
}
