package com.strangegrotto.wealthdraft.projections.impl;

import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import com.strangegrotto.wealthdraft.errors.ValOrGerr;
import com.strangegrotto.wealthdraft.projections.impl.SerProjectionScenario;
import org.immutables.value.Value;

import java.math.BigDecimal;
import java.util.Map;

@WealthdraftImmutableStyle
@Value.Immutable
// Deserialized via custom deserializer
public interface SerProjections {
    BigDecimal getDefaultAnnualGrowth();

    @Value.Parameter
    Map<String, ValOrGerr<SerProjectionScenario>> getScenarios();
}
