package com.strangegrotto.wealthdraft.backend.projections.impl;

import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import com.strangegrotto.wealthdraft.backend.projections.api.types.ProjectionScenario;
import org.immutables.value.Value;

import java.util.Optional;

@WealthdraftImmutableStyle
@Value.Immutable
interface SerProjectionScenario extends ProjectionScenario  {
    @Override
    @Value.Parameter
    Optional<String> getBase();
}
