package com.strangegrotto.wealthdraft.backend.projections.impl;

import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import com.strangegrotto.wealthdraft.backend.projections.api.types.AssetChange;
import com.strangegrotto.wealthdraft.backend.projections.api.types.ProjectionScenario;
import org.immutables.value.Value;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;

@WealthdraftImmutableStyle
@Value.Immutable
interface SerProjectionScenario extends ProjectionScenario  {
    @Override
    String getName();

    @Value.Parameter
    @Override
    Optional<String> getBase();

    @Override
    SortedMap<LocalDate, Map<String, AssetChange>> getAssetChanges();
}
