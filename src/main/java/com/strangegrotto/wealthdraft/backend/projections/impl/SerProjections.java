package com.strangegrotto.wealthdraft.backend.projections.impl;

import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import org.immutables.value.Value;

import java.math.BigDecimal;
import java.util.Map;

@WealthdraftImmutableStyle
@Value.Immutable
// Deserialized via custom deserializer
public interface SerProjections {
    BigDecimal getDefaultAnnualGrowth();

    @Value.Parameter
    Map<String, SerProjectionScenario> getScenarios();
}
