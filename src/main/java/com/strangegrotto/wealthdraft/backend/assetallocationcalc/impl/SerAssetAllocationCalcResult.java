package com.strangegrotto.wealthdraft.backend.assetallocationcalc.impl;

import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import com.strangegrotto.wealthdraft.backend.assetallocationcalc.api.types.AssetAllocationCalcResult;
import org.immutables.value.Value;

@WealthdraftImmutableStyle
@Value.Immutable
interface SerAssetAllocationCalcResult extends AssetAllocationCalcResult {
}
