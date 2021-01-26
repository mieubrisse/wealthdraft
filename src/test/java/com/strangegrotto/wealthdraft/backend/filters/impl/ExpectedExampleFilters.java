package com.strangegrotto.wealthdraft.backend.filters.impl;

import com.strangegrotto.wealthdraft.backend.assets.api.types.AssetType;
import com.strangegrotto.wealthdraft.backend.assets.impl.ExpectedExampleAssetDefinitions;
import com.strangegrotto.wealthdraft.backend.filters.api.types.AssetFilter;
import com.strangegrotto.wealthdraft.backend.filters.impl.ImmConjunctionAssetFilter;
import com.strangegrotto.wealthdraft.backend.filters.impl.ImmTagAssetFilter;
import com.strangegrotto.wealthdraft.backend.filters.impl.SerAssetFilter;
import com.strangegrotto.wealthdraft.backend.tags.custom.impl.ExpectedExampleCustomTags;
import com.strangegrotto.wealthdraft.backend.tags.intrinsic.IntrinsicAssetTag;

import java.util.List;
import java.util.Map;

public class ExpectedExampleFilters {
    public static final String DOMESTIC_ASSETS_FILTER_ID = "domesticAssets";
    public static final String INTERNATIONAL_ASSETS_FILTER_ID = "internationalAssets";
    public static final String DOMESTIC_BANK_ASSETS_FILTER_ID = "domesticBankAssets";

    public static final SerAssetFilter DOMESTIC_ASSETS_FILTER = ImmTagAssetFilter.of(
            ExpectedExampleCustomTags.DOM_OR_INTL_TAG,
            ExpectedExampleCustomTags.DOMESTIC_ASSET_TAG_VALUE
    );
    public static final SerAssetFilter INTERNATIONAL_ASSETS_FILTER = ImmTagAssetFilter.of(
            ExpectedExampleCustomTags.DOM_OR_INTL_TAG,
            ExpectedExampleCustomTags.INTERNATIONAL_ASSET_TAG_VALUE
    );
    public static final SerAssetFilter DOMESTIC_BANK_ASSETS_FILTER = ImmConjunctionAssetFilter.of(List.of(
            ImmTagAssetFilter.of(
                    ExpectedExampleCustomTags.DOM_OR_INTL_TAG,
                    ExpectedExampleCustomTags.DOMESTIC_ASSET_TAG_VALUE
            ),
            ImmTagAssetFilter.of(
                    IntrinsicAssetTag.ASSET_TYPE.getTagName(),
                    AssetType.BANK_ACCOUNT.name()
            )
    ));

    public static final Map<String, AssetFilter> EXPECTED_FILTERS = Map.of(
            DOMESTIC_ASSETS_FILTER_ID, DOMESTIC_ASSETS_FILTER,
            INTERNATIONAL_ASSETS_FILTER_ID, INTERNATIONAL_ASSETS_FILTER,
            DOMESTIC_BANK_ASSETS_FILTER_ID, DOMESTIC_BANK_ASSETS_FILTER
    );
}
