package com.strangegrotto.wealthdraft.assetallocationcalc.api;

import com.strangegrotto.wealthdraft.assetallocation.api.types.TargetAssetAllocation;
import com.strangegrotto.wealthdraft.assetallocationcalc.api.types.AssetAllocationCalcResult;

public interface AssetAllocationCalculator {
    AssetAllocationCalcResult calculate(TargetAssetAllocation target);
}
