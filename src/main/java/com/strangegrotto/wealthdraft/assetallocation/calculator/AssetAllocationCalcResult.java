package com.strangegrotto.wealthdraft.assetallocation.calculator;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import org.immutables.value.Value;

@WealthdraftImmutableStyle
@Value.Immutable
@JsonDeserialize(as = ImmAssetAllocationCalcResults.class)
public interface AssetAllocationCalcResult {
    getNumeratorFilterName();

    getDenominatorFilterName();
}
