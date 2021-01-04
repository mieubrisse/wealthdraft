package com.strangegrotto.wealthdraft.assets.definition;

import com.strangegrotto.wealthdraft.assetimpls.bankaccount.BankAccountAsset;
import com.strangegrotto.wealthdraft.assetimpls.bankaccount.ImmBankAccountAsset;
import com.strangegrotto.wealthdraft.assetimpls.stock.ImmStockAsset;

import java.util.Map;
import java.util.Set;

public class ExpectedExampleAssetDefinitions {
    // Custom tags
    public static final String DOM_OR_INTL_TAG = "domOrIntl";
    public static final String DOMESTIC_ASSET_TAG_VALUE = "domestic";
    public static final String INTERNATIONAL_ASSET_TAG_VALUE = "international";

    public static final String BROKER_TAG = "broker";

    public static final Map<String, CustomTagDefinition> EXPECTED_CUSTOM_TAGS = Map.of(
            DOM_OR_INTL_TAG, ImmCustomTagDefinition
                    .of(Set.of(DOMESTIC_ASSET_TAG_VALUE, INTERNATIONAL_ASSET_TAG_VALUE))
                    .withRequired(true),
            BROKER_TAG, ImmCustomTagDefinition.of(Set.of())
    );

    public static final String RETIREMENT_ACCOUNT_ID = "ret";
    public static final String BROKERAGE_ACCOUNT_ID = "pbrok";
    public static final String BANK_ACCOUNT_ID = "bank";
    public static final String BITCOIN_HOLDING_ID = "btc";



    public static final Asset RETIREMENT_ASSET = ImmStockAsset.of("Retirement account").withCustomTags(Map.of(
            BROKER_TAG, "Fidelity",
            DOM_OR_INTL_TAG, DOMESTIC_ASSET_TAG_VALUE
    ));
    public static final Asset BROKERAGE_ACCOUNT_ASSET = ImmStockAsset.of("Personal brokerage account").withCustomTags(Map.of(
            BROKER_TAG, "Vanguard",
            DOM_OR_INTL_TAG, DOMESTIC_ASSET_TAG_VALUE
    ));
    public static final Asset BANK_ACCOUNT_ASSET = ImmBankAccountAsset.of("Bank account").withCustomTags(Map.of(
            BROKER_TAG, "Chase",
            DOM_OR_INTL_TAG, DOMESTIC_ASSET_TAG_VALUE
    ));
    public static final Asset BITCOIN_ASSET = ImmBankAccountAsset.of("BTC holdings").withCustomTags(Map.of(
            DOM_OR_INTL_TAG, INTERNATIONAL_ASSET_TAG_VALUE
    ));

    public static final Map<String, Asset> EXPECTED_ASSETS = Map.of(
            RETIREMENT_ACCOUNT_ID, RETIREMENT_ASSET,
            BROKERAGE_ACCOUNT_ID, BROKERAGE_ACCOUNT_ASSET,
            BANK_ACCOUNT_ID, BANK_ACCOUNT_ASSET,
            BITCOIN_HOLDING_ID, BITCOIN_ASSET
    );


}
