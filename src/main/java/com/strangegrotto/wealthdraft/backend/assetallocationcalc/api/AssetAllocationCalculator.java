package com.strangegrotto.wealthdraft.backend.assetallocationcalc.api;

import com.strangegrotto.wealthdraft.backend.assetallocation.api.types.TargetAssetAllocation;
import com.strangegrotto.wealthdraft.backend.assetallocationcalc.api.types.AssetAllocationCalcResult;

public interface AssetAllocationCalculator {
    AssetAllocationCalcResult calculate(TargetAssetAllocation target);
}
