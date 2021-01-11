package com.strangegrotto.wealthdraft.assethistory.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import com.strangegrotto.wealthdraft.assets.impl.SerAsset;
import com.strangegrotto.wealthdraft.assethistory.api.types.AssetSnapshot;
import org.immutables.value.Value;

import java.time.LocalDate;
import java.util.Map;
import java.util.SortedMap;

@WealthdraftImmutableStyle
@Value.Immutable
// Deserialized using custom deserializer without default constructor
public abstract class SerAssetsHistory {
    @Value.Parameter
    public abstract SortedMap<LocalDate, Map<String, AssetSnapshot<?>>> getHistory();
}