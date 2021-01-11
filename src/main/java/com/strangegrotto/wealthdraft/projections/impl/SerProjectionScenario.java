package com.strangegrotto.wealthdraft.projections.impl;

import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import com.strangegrotto.wealthdraft.projections.api.types.ProjectionScenario;
import com.strangegrotto.wealthdraft.projections.impl.temporal.AssetChange;
import org.immutables.value.Value;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;

@WealthdraftImmutableStyle
@Value.Immutable
public interface SerProjectionScenario extends ProjectionScenario  { }
