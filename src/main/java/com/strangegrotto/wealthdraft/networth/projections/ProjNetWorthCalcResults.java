package com.strangegrotto.wealthdraft.networth.projections;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.time.LocalDate;
import java.util.Map;
import java.util.SortedMap;

@Value.Immutable
@JsonDeserialize(as = ImmutableProjNetWorthCalcResults.class)
public interface ProjNetWorthCalcResults {
    Map<String, SortedMap<LocalDate, Long>> projectionsNetWorth();
}
