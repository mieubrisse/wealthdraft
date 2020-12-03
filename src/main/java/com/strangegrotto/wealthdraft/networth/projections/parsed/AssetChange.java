package com.strangegrotto.wealthdraft.networth.projections.parsed;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableAssetChange.class)
public interface AssetChange {
}
