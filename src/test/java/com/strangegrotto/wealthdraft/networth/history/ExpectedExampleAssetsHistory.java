package com.strangegrotto.wealthdraft.networth.history;

import com.strangegrotto.wealthdraft.assetimpls.bankaccount.BankAccountAssetSnapshot;
import com.strangegrotto.wealthdraft.assets.definition.ExpectedExampleAssetDefinitions;
import com.strangegrotto.wealthdraft.assets.temporal.AssetSnapshot;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class ExpectedExampleAssetsHistory {

    public static final Map<String, SortedMap<LocalDate, AssetSnapshot>> EXPECTED_ASSETS_HISTORY;

    static {
        var retirementHistory = new TreeMap<LocalDate, AssetSnapshot>(Map.of(
                LocalDate.of(2020, 6, 1), new BankAccountAssetSnapshot(new BigDecimal(150000), new BigDecimal("0.05")),
                LocalDate.of(2020, 8, 15), new BankAccountAssetSnapshot(new BigDecimal(150000), new BigDecimal("0.06"))
        ));

        var brokerageHistory = new TreeMap<LocalDate, AssetSnapshot>(Map.of(
                LocalDate.of(2020, 6, 1), new BankAccountAssetSnapshot(new BigDecimal(20000), new BigDecimal("0.03")),
                LocalDate.of(2020, 11, 30), new BankAccountAssetSnapshot(new BigDecimal(22000), new BigDecimal("0.03"))
        ));

        var bankAccountHistory = new TreeMap<LocalDate, AssetSnapshot>(Map.of(
                LocalDate.of(2020, 6, 1), new BankAccountAssetSnapshot(new BigDecimal(4000), new BigDecimal("0.02")),
                LocalDate.of(2020, 11, 30), new BankAccountAssetSnapshot(new BigDecimal(12000), new BigDecimal("0.02"))
        ));

        var bitcoinHistory = new TreeMap<LocalDate, AssetSnapshot>(Map.of(
                LocalDate.of(2020, 6, 1), new BankAccountAssetSnapshot(new BigDecimal(13000), new BigDecimal("0.0")),
                LocalDate.of(2020, 11, 30), new BankAccountAssetSnapshot(new BigDecimal(15000), new BigDecimal("0.0"))
        ));

        // ---------------------------------------------------------------------------------------------
        EXPECTED_ASSETS_HISTORY = Map.of(
                ExpectedExampleAssetDefinitions.RETIREMENT_ACCOUNT_ID, retirementHistory,
                ExpectedExampleAssetDefinitions.BROKERAGE_ACCOUNT_ID, brokerageHistory,
                ExpectedExampleAssetDefinitions.BANK_ACCOUNT_ID, bankAccountHistory,
                ExpectedExampleAssetDefinitions.BITCOIN_HOLDING_ID, bitcoinHistory
        );
    }
}
