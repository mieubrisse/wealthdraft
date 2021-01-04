package com.strangegrotto.wealthdraft.assetallocation;

import com.strangegrotto.wealthdraft.assetimpls.AssetTypeTagValue;
import com.strangegrotto.wealthdraft.assets.definition.ExpectedExampleAssetDefinitions;
import com.strangegrotto.wealthdraft.assets.definition.IntrinsicAssetTag;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class ExpectedExampleTargetAssetAllocations {
    private static final TargetAssetAllocation ALLOCATION_1 = ImmTargetAssetAllocation.of(
            ImmConjunctiveAssetTagFilter.of(Map.of(
                    ExpectedExampleAssetDefinitions.DOM_OR_INTL_TAG,
                    ExpectedExampleAssetDefinitions.DOMESTIC_ASSET_TAG_VALUE
            )),
            BigDecimal.valueOf(0.7)
    );

    private static final TargetAssetAllocation ALLOCATION_2 = ImmTargetAssetAllocation.of(
            ImmConjunctiveAssetTagFilter.of(Map.of(
                    ExpectedExampleAssetDefinitions.DOM_OR_INTL_TAG,
                    ExpectedExampleAssetDefinitions.INTERNATIONAL_ASSET_TAG_VALUE
            )),
            BigDecimal.valueOf(0.3)
    );

    private static final TargetAssetAllocation ALLOCATION_3 = ImmTargetAssetAllocation.of(
            ImmConjunctiveAssetTagFilter.of(Map.of(
                    ExpectedExampleAssetDefinitions.DOM_OR_INTL_TAG, ExpectedExampleAssetDefinitions.DOMESTIC_ASSET_TAG_VALUE,
                    IntrinsicAssetTag.ASSET_TYPE.getTagName(), AssetTypeTagValue.BANK_ACCOUNT.name()
            )),
            BigDecimal.valueOf(0.01)
    );

    public static final List<TargetAssetAllocation> ASSET_ALLOCATIONS = List.of(
            ALLOCATION_1,
            ALLOCATION_2,
            ALLOCATION_3
    );
}
