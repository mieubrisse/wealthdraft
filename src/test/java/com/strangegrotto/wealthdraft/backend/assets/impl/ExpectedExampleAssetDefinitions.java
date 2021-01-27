package com.strangegrotto.wealthdraft.backend.assets.impl;

import com.strangegrotto.wealthdraft.backend.assets.api.types.AssetType;
import com.strangegrotto.wealthdraft.backend.tags.custom.impl.ExpectedExampleCustomTags;

import java.util.Map;

public class ExpectedExampleAssetDefinitions {
    public static final String RETIREMENT_ACCOUNT_ID = "ret";
    public static final String BROKERAGE_ACCOUNT_ID = "pbrok";
    public static final String BANK_ACCOUNT_ID = "bank";
    public static final String BITCOIN_HOLDING_ID = "btc";

    public static final SerAsset RETIREMENT_ASSET = ImmSerAsset.of("Retirement account", AssetType.STOCK).withCustomTags(Map.of(
            ExpectedExampleCustomTags.BROKER_TAG, "Fidelity",
            ExpectedExampleCustomTags.DOM_OR_INTL_TAG, ExpectedExampleCustomTags.DOMESTIC_ASSET_TAG_VALUE,
            ExpectedExampleCustomTags.IS_RETIREMENT_TAG, ExpectedExampleCustomTags.IS_RETIREMENT_TAG_VALUE
    ));
    public static final SerAsset BROKERAGE_ACCOUNT_ASSET = ImmSerAsset.of("Personal brokerage account", AssetType.STOCK).withCustomTags(Map.of(
            ExpectedExampleCustomTags.BROKER_TAG, "Vanguard",
            ExpectedExampleCustomTags.DOM_OR_INTL_TAG, ExpectedExampleCustomTags.DOMESTIC_ASSET_TAG_VALUE,
            ExpectedExampleCustomTags.IS_RETIREMENT_TAG, ExpectedExampleCustomTags.IS_NOT_RETIREMENT_TAG_VALUE
    ));
    public static final SerAsset BANK_ACCOUNT_ASSET = ImmSerAsset.of("Bank account", AssetType.BANK_ACCOUNT).withCustomTags(Map.of(
            ExpectedExampleCustomTags.BROKER_TAG, "Chase",
            ExpectedExampleCustomTags.DOM_OR_INTL_TAG, ExpectedExampleCustomTags.DOMESTIC_ASSET_TAG_VALUE,
            ExpectedExampleCustomTags.IS_RETIREMENT_TAG, ExpectedExampleCustomTags.IS_NOT_RETIREMENT_TAG_VALUE
    ));
    public static final SerAsset BITCOIN_ASSET = ImmSerAsset.of("BTC holdings", AssetType.BANK_ACCOUNT).withCustomTags(Map.of(
            ExpectedExampleCustomTags.DOM_OR_INTL_TAG, ExpectedExampleCustomTags.INTERNATIONAL_ASSET_TAG_VALUE,
            ExpectedExampleCustomTags.IS_RETIREMENT_TAG, ExpectedExampleCustomTags.IS_NOT_RETIREMENT_TAG_VALUE
    ));

    public static final Map<String, SerAsset> EXPECTED_ASSETS = Map.of(
            RETIREMENT_ACCOUNT_ID, RETIREMENT_ASSET,
            BROKERAGE_ACCOUNT_ID, BROKERAGE_ACCOUNT_ASSET,
            BANK_ACCOUNT_ID, BANK_ACCOUNT_ASSET,
            BITCOIN_HOLDING_ID, BITCOIN_ASSET
    );
}
