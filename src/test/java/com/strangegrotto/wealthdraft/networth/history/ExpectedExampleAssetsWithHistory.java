package com.strangegrotto.wealthdraft.networth.history;

import com.strangegrotto.wealthdraft.assets.Asset;
import com.strangegrotto.wealthdraft.assets.AssetSnapshot;
import com.strangegrotto.wealthdraft.assets.bankaccount.ImmBankAccountAsset;
import com.strangegrotto.wealthdraft.assets.bankaccount.ImmBankAccountAssetSnapshot;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class ExpectedExampleAssetsWithHistory {
    public static final String RETIREMENT_ACCOUNT_ID = "ret";
    public static final String BROKERAGE_ACCOUNT_ID = "pbrok";
    public static final String BANK_ACCOUNT_ID = "bank";
    public static final String BITCOIN_HOLDING_ID = "btc";

    // Custom tags
    private static final String DOM_OR_INTL_TAG = "domOrIntl";
    private static final String DOMESTIC_ASSET_TAG_VALUE = "domestic";
    private static final String INTERNATIONAL_ASSET_TAG_VALUE = "international";
    private static final String BROKER_TAG = "broker";


    public static final Asset RETIREMENT_ASSET = ImmBankAccountAsset.of("Retirement account").withCustomTags(Map.of(
            BROKER_TAG, "Fidelity",
            DOM_OR_INTL_TAG, DOMESTIC_ASSET_TAG_VALUE
    ));
    public static final Asset BROKERAGE_ACCOUNT_ASSET = ImmBankAccountAsset.of("Personal brokerage account").withCustomTags(Map.of(
            BROKER_TAG, "Vanguard",
            DOM_OR_INTL_TAG, DOMESTIC_ASSET_TAG_VALUE
    ));
    public static final Asset BANK_ACCOUNT_ASSET = ImmBankAccountAsset.of("Bank account").withCustomTags(Map.of(
            BROKER_TAG, "Chase",
            DOM_OR_INTL_TAG, INTERNATIONAL_ASSET_TAG_VALUE
    ));
    public static final Asset BITCOIN_ASSET = ImmBankAccountAsset.of("BTC holdings");

    public static final Map<String, Asset> EXPECTED_ASSETS = Map.of(
            RETIREMENT_ACCOUNT_ID, RETIREMENT_ASSET,
            BROKERAGE_ACCOUNT_ID, BROKERAGE_ACCOUNT_ASSET,
            BANK_ACCOUNT_ID, BANK_ACCOUNT_ASSET,
            BITCOIN_HOLDING_ID, BITCOIN_ASSET
    );

    public static final Map<String, SortedMap<LocalDate, AssetSnapshot>> EXPECTED_ASSET_HISTORY;

    static {
        var retirementHistory = new TreeMap<LocalDate, AssetSnapshot>(Map.of(
                LocalDate.of(2020, 6, 1), ImmBankAccountAssetSnapshot.of(new BigDecimal(150000), new BigDecimal("0.05")),
                LocalDate.of(2020, 8, 15), ImmBankAccountAssetSnapshot.of(new BigDecimal(150000), new BigDecimal("0.06"))
        ));

        var brokerageHistory = new TreeMap<LocalDate, AssetSnapshot>(Map.of(
                LocalDate.of(2020, 6, 1), ImmBankAccountAssetSnapshot.of(new BigDecimal(20000), new BigDecimal("0.03")),
                LocalDate.of(2020, 11, 30), ImmBankAccountAssetSnapshot.of(new BigDecimal(22000), new BigDecimal("0.03"))
        ));

        var bankAccountHistory = new TreeMap<LocalDate, AssetSnapshot>(Map.of(
                LocalDate.of(2020, 6, 1), ImmBankAccountAssetSnapshot.of(new BigDecimal(4000), new BigDecimal("0.02")),
                LocalDate.of(2020, 11, 30), ImmBankAccountAssetSnapshot.of(new BigDecimal(12000), new BigDecimal("0.02"))
        ));

        var bitcoinHistory = new TreeMap<LocalDate, AssetSnapshot>(Map.of(
                LocalDate.of(2020, 6, 1), ImmBankAccountAssetSnapshot.of(new BigDecimal(13000), new BigDecimal("0.0")),
                LocalDate.of(2020, 11, 30), ImmBankAccountAssetSnapshot.of(new BigDecimal(15000), new BigDecimal("0.0"))
        ));

        // ---------------------------------------------------------------------------------------------
        EXPECTED_ASSET_HISTORY = Map.of(
                RETIREMENT_ACCOUNT_ID, retirementHistory,
                BROKERAGE_ACCOUNT_ID, brokerageHistory,
                BANK_ACCOUNT_ID, bankAccountHistory,
                BITCOIN_HOLDING_ID, bitcoinHistory
        );
    }
}
