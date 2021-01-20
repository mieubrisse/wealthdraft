package com.strangegrotto.wealthdraft.backend.projections.api.types;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;

public interface ProjectionScenario {
    String getName();

    Optional<String> getBase();

    SortedMap<LocalDate, Map<String, AssetChange>> getAssetChanges();
}
