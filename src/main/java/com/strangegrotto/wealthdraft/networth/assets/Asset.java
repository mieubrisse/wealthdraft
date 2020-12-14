package com.strangegrotto.wealthdraft.networth.assets;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import org.immutables.value.Value;

/**
 * Contains metadata about an asset
 */
@WealthdraftImmutableStyle
@Value.Immutable
@JsonDeserialize(as = ImmAsset.class)
public interface Asset {
    String getName();

    AssetType getType();
}
