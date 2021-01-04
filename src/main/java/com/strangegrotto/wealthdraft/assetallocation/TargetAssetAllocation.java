package com.strangegrotto.wealthdraft.assetallocation;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import org.immutables.value.Value;

import java.math.BigDecimal;

@WealthdraftImmutableStyle
@Value.Immutable
@JsonDeserialize(as = ImmTargetAssetAllocation.class)
public interface TargetAssetAllocation {
    AssetFilter getFilter();

    BigDecimal getPercentagePortfolio();
}
