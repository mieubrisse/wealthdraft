package com.strangegrotto.wealthdraft.backend.assetallocation.impl;

import com.strangegrotto.wealthdraft.backend.assetallocation.api.types.TargetAssetAllocation;
import com.strangegrotto.wealthdraft.backend.filters.impl.ExpectedExampleFilters;

import java.math.BigDecimal;
import java.util.List;

public class ExpectedExampleTargetAssetAllocations {

    private static final SerTargetAssetAllocation TARGET_ALLOCATION_1 = ImmSerTargetAssetAllocation.of(
            ExpectedExampleFilters.DOMESTIC_ASSETS_FILTER_ID,
            BigDecimal.valueOf(0.7)
    );

    private static final SerTargetAssetAllocation TARGET_ALLOCATION_2 = ImmSerTargetAssetAllocation.of(
            ExpectedExampleFilters.INTERNATIONAL_ASSETS_FILTER_ID,
            BigDecimal.valueOf(0.3)
    );

    private static final SerTargetAssetAllocation TARGET_ALLOCATION_3 = ImmSerTargetAssetAllocation.of(
            ExpectedExampleFilters.DOMESTIC_BANK_ASSETS_FILTER_ID,
            BigDecimal.valueOf(0.01)
    ).withDenominatorFilterIdOpt(ExpectedExampleFilters.DOMESTIC_ASSETS_FILTER_ID);

    public static final List<TargetAssetAllocation> TARGET_ALLOCATIONS = List.of(
                    TARGET_ALLOCATION_1,
                    TARGET_ALLOCATION_2,
                    TARGET_ALLOCATION_3
    );
}
