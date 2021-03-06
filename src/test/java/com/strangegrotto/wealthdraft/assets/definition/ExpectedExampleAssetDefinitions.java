package com.strangegrotto.wealthdraft.assets.definition;

import com.strangegrotto.wealthdraft.assetimpls.AssetType;

import java.util.Map;

public class ExpectedExampleAssetDefinitions {
    // Custom tags
    public static final String DOM_OR_INTL_TAG = "domOrIntl";
    public static final String DOMESTIC_ASSET_TAG_VALUE = "domestic";
    public static final String INTERNATIONAL_ASSET_TAG_VALUE = "international";

    public static final String BROKER_TAG = "broker";

    public static final Map<String, CustomTagDefinition> EXPECTED_CUSTOM_TAGS = Map.of(
            DOM_OR_INTL_TAG, ImmCustomTagDefinition.builder()
                    .addAllowedValues(DOMESTIC_ASSET_TAG_VALUE, INTERNATIONAL_ASSET_TAG_VALUE)
                    .defaultValue(DOMESTIC_ASSET_TAG_VALUE)
                    .required(true)
                    .build(),
            BROKER_TAG, ImmCustomTagDefinition.builder().build()
    );

    public static final String RETIREMENT_ACCOUNT_ID = "ret";
    public static final String BROKERAGE_ACCOUNT_ID = "pbrok";
    public static final String BANK_ACCOUNT_ID = "bank";
    public static final String BITCOIN_HOLDING_ID = "btc";



    public static final Asset RETIREMENT_ASSET = ImmAsset.of("Retirement account", AssetType.STOCK).withCustomTags(Map.of(
            BROKER_TAG, "Fidelity",
            DOM_OR_INTL_TAG, DOMESTIC_ASSET_TAG_VALUE
    ));
    public static final Asset BROKERAGE_ACCOUNT_ASSET = ImmAsset.of("Personal brokerage account", AssetType.STOCK).withCustomTags(Map.of(
            BROKER_TAG, "Vanguard",
            DOM_OR_INTL_TAG, DOMESTIC_ASSET_TAG_VALUE
    ));
    public static final Asset BANK_ACCOUNT_ASSET = ImmAsset.of("Bank account", AssetType.BANK_ACCOUNT).withCustomTags(Map.of(
            BROKER_TAG, "Chase",
            DOM_OR_INTL_TAG, DOMESTIC_ASSET_TAG_VALUE
    ));
    public static final Asset BITCOIN_ASSET = ImmAsset.of("BTC holdings", AssetType.BANK_ACCOUNT).withCustomTags(Map.of(
            DOM_OR_INTL_TAG, INTERNATIONAL_ASSET_TAG_VALUE
    ));

    public static final Map<String, Asset> EXPECTED_ASSETS = Map.of(
            RETIREMENT_ACCOUNT_ID, RETIREMENT_ASSET,
            BROKERAGE_ACCOUNT_ID, BROKERAGE_ACCOUNT_ASSET,
            BANK_ACCOUNT_ID, BANK_ACCOUNT_ASSET,
            BITCOIN_HOLDING_ID, BITCOIN_ASSET
    );


}
