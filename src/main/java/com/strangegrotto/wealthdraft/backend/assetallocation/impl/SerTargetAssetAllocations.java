package com.strangegrotto.wealthdraft.backend.assetallocation.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import org.immutables.value.Value;

import java.util.List;

@WealthdraftImmutableStyle
@Value.Immutable
// Deserialized using custom deserializer
public interface SerTargetAssetAllocations {
    // TODO add configurable WARN/ERROR thresholds

    @Value.Parameter
    @JsonProperty("targets")
    List<SerTargetAssetAllocation> getTargets();
}
