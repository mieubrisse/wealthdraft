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
    public static final String DOMESTIC_ASSETS_FILTER_ID = "domesticAssets";
    public static final String INTERNATIONAL_ASSETS_FILTER_ID = "internationalAssets";
    public static final String DOMESTIC_BANK_ASSETS_FILTER_ID = "domesticBankAssets";

    public static final SerAssetFilter DOMESTIC_ASSETS_FILTER = ImmTagAssetFilter.of(
            ExpectedExampleAssetDefinitions.EXPECTED_CUSTOM_TAGS,
            ExpectedExampleAssetDefinitions.DOM_OR_INTL_TAG,
            ExpectedExampleAssetDefinitions.DOMESTIC_ASSET_TAG_VALUE
    );
    public static final SerAssetFilter INTERNATIONAL_ASSETS_FILTER = ImmTagAssetFilter.of(
            ExpectedExampleAssetDefinitions.EXPECTED_CUSTOM_TAGS,
            ExpectedExampleAssetDefinitions.DOM_OR_INTL_TAG,
            ExpectedExampleAssetDefinitions.INTERNATIONAL_ASSET_TAG_VALUE
    );
    public static final SerAssetFilter DOMESTIC_BANK_ASSETS_FILTER = ImmConjunctionAssetFilter.of(List.of(
            ImmTagAssetFilter.of(
                    ExpectedExampleAssetDefinitions.EXPECTED_CUSTOM_TAGS,
                    ExpectedExampleAssetDefinitions.DOM_OR_INTL_TAG,
                    ExpectedExampleAssetDefinitions.DOMESTIC_ASSET_TAG_VALUE
            ),
            ImmTagAssetFilter.of(
                    ExpectedExampleAssetDefinitions.EXPECTED_CUSTOM_TAGS,
                    IntrinsicAssetTag.ASSET_TYPE.getTagName(),
                    AssetType.BANK_ACCOUNT.name()
            )
    ));

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
            Map.of(
                DOMESTIC_ASSETS_FILTER_ID, DOMESTIC_ASSETS_FILTER,
                INTERNATIONAL_ASSETS_FILTER_ID, INTERNATIONAL_ASSETS_FILTER,
                DOMESTIC_BANK_ASSETS_FILTER_ID, DOMESTIC_BANK_ASSETS_FILTER
            ),
            List.of(
                    TARGET_ALLOCATION_1,
                    TARGET_ALLOCATION_2,
                    TARGET_ALLOCATION_3
            )
    );
}
