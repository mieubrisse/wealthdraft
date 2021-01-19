package com.strangegrotto.wealthdraft.assetallocation.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import com.strangegrotto.wealthdraft.filters.impl.SerAssetFilter;
import org.immutables.value.Value;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@WealthdraftImmutableStyle
@Value.Immutable
// Deserialized using custom deserializer
public interface SerTargetAssetAllocations {
    // TODO add configurable WARN/ERROR thresholds

    @Value.Parameter
    @JsonProperty("targets")
    List<SerTargetAssetAllocation> getTargets();
}
