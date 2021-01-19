package com.strangegrotto.wealthdraft.assetallocation.api;

import com.strangegrotto.wealthdraft.assetallocation.api.types.TargetAssetAllocation;

import java.util.List;

public interface TargetAssetAllocationsStore {
    // TODO add configurable threshold parameters

    List<TargetAssetAllocation> getTargetAllocations();
}
