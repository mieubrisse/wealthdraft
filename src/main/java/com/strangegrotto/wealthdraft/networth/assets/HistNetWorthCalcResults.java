package com.strangegrotto.wealthdraft.networth.assets;

import com.strangegrotto.wealthdraft.validator.ValidationWarning;
import org.immutables.value.Value;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

@Value.Immutable
public interface HistNetWorthCalcResults {
    List<ValidationWarning> getValidationWarnings();

    SortedMap<LocalDate, Long> getHistoricalNetWorth();

    Map<String, Long> getLatestAssetValues();
}
