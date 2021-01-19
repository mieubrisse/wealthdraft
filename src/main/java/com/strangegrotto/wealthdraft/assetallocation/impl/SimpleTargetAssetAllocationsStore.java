package com.strangegrotto.wealthdraft.assetallocation.impl;

import com.strangegrotto.wealthdraft.assetallocation.api.TargetAssetAllocationsStore;
import com.strangegrotto.wealthdraft.assetallocation.api.types.TargetAssetAllocation;

import java.util.List;

public class SimpleTargetAssetAllocationsStore implements TargetAssetAllocationsStore {
    private final List<TargetAssetAllocation> targetAssetAllocations;

    public SimpleTargetAssetAllocationsStore(List<TargetAssetAllocation> targetAssetAllocations) {
        this.targetAssetAllocations = List.copyOf(targetAssetAllocations);
    }

    @Override
    public List<TargetAssetAllocation> getTargetAllocations() {
        return this.targetAssetAllocations;
    }
}
