package com.strangegrotto.wealthdraft.networth;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.strangegrotto.wealthdraft.validator.ValidationWarning;
import org.immutables.value.Value;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Value.Immutable
@JsonDeserialize(as = ImmutableNetWorthCalculationResults.class)
public interface NetWorthCalculationResults {
    List<ValidationWarning> getWarnings();

    Map<LocalDate, Long> getHistoricalNetWorth();

    Map<String, Map<LocalDate, Long>> projectionsNetWorth();
}
