package com.strangegrotto.wealthdraft.backend.projections.impl;

import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import com.strangegrotto.wealthdraft.backend.projections.api.types.ProjectionScenario;
import org.immutables.value.Value;

@WealthdraftImmutableStyle
@Value.Immutable
public interface SerProjectionScenario extends ProjectionScenario  { }
