package com.strangegrotto.wealthdraft.backend.assetallocation;

import com.strangegrotto.wealthdraft.backend.assetallocation.datamodel.ImmTargetAssetAllocation;
import com.strangegrotto.wealthdraft.backend.assetallocation.datamodel.ImmTargetAssetAllocations;
import com.strangegrotto.wealthdraft.backend.assetallocation.impl.SerTargetAssetAllocation;
import com.strangegrotto.wealthdraft.backend.assetallocation.impl.SerTargetAssetAllocations;
import com.strangegrotto.wealthdraft.backend.filters.impl.SerAssetFilter;
import com.strangegrotto.wealthdraft.backend.filters.ImmConjunctionAssetFilter;
import com.strangegrotto.wealthdraft.backend.filters.ImmTagAssetFilter;
import com.strangegrotto.wealthdraft.backend.assets.api.types.AssetType;
import com.strangegrotto.wealthdraft.backend.assets.impl.ExpectedExampleAssetDefinitions;
import com.strangegrotto.wealthdraft.backend.tags.intrinsic.IntrinsicAssetTag;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class ExpectedExampleTargetAssetAllocations {

    private static final SerTargetAssetAllocation TARGET_ALLOCATION_1 = ImmTargetAssetAllocation.of(
            DOMESTIC_ASSETS_FILTER_ID,
            BigDecimal.valueOf(0.7)
    );

    private static final SerTargetAssetAllocation TARGET_ALLOCATION_2 = ImmTargetAssetAllocation.of(
            INTERNATIONAL_ASSETS_FILTER_ID,
            BigDecimal.valueOf(0.3)
    );

    private static final SerTargetAssetAllocation TARGET_ALLOCATION_3 = ImmTargetAssetAllocation.of(
            DOMESTIC_BANK_ASSETS_FILTER_ID,
            BigDecimal.valueOf(0.01)
    ).withDenominatorFilterOpt(DOMESTIC_ASSETS_FILTER_ID);

    public static final SerTargetAssetAllocations ASSET_ALLOCATIONS = ImmTargetAssetAllocations.of(
            List.of(
                    TARGET_ALLOCATION_1,
                    TARGET_ALLOCATION_2,
                    TARGET_ALLOCATION_3
            )
    );
}
