package com.strangegrotto.wealthdraft.networth;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableAsset.class)
public interface Asset {
    String getName();

    AssetType getType();
}
