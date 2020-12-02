package com.strangegrotto.wealthdraft.assets;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Value.Immutable
@JsonDeserialize(as = ImmutableAsset.class)
public interface Asset {
    String getName();

    Map<LocalDate, Long> getHistorical();
}
