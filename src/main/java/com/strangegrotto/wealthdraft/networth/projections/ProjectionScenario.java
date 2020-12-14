package com.strangegrotto.wealthdraft.networth.projections;

import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import org.immutables.value.Value;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;

@WealthdraftImmutableStyle
@Value.Immutable
public interface ProjectionScenario {
    String getName();

    Optional<String> getBase();

    SortedMap<LocalDate, Map<String, AssetChange>> getAssetChanges();
}
