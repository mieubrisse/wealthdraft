package com.strangegrotto.wealthdraft.networth.projections;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.strangegrotto.wealthdraft.errors.ValOrGerr;
import org.immutables.value.Value;

import java.time.LocalDate;
import java.util.Map;
import java.util.SortedMap;

@Value.Immutable
@JsonDeserialize(as = ImmutableProjNetWorthCalcResults.class)
public interface ProjNetWorthCalcResults {
    // Mapping of scenario ID -> (projected_net_worth_values OR error)
    Map<String, ValOrGerr<SortedMap<LocalDate, Long>>> getProjNetWorths();
}
