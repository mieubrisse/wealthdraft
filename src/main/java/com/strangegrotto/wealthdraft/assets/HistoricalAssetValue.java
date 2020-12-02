package com.strangegrotto.wealthdraft.assets;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.time.LocalDate;

@Value.Immutable
@JsonDeserialize(as = ImmutableHistoricalAssetValue.class)
public interface HistoricalAssetValue {
    LocalDate getDate();
    long getValue();
}
