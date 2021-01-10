package com.strangegrotto.wealthdraft.networth.projections;

import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import com.strangegrotto.wealthdraft.errors.ValOrGerr;
import org.immutables.value.Value;

import java.math.BigDecimal;
import java.util.Map;

@WealthdraftImmutableStyle
@Value.Immutable
// Deserialized via custom deserializer
public interface Projections {
    BigDecimal getDefaultAnnualGrowth();

    @Value.Parameter
    Map<String, ValOrGerr<ProjectionScenario>> getScenarios();
}
