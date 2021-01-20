package com.strangegrotto.wealthdraft.networth.history;

import com.strangegrotto.wealthdraft.assetimpls.bankaccount.ImmBankAccountAssetSnapshot;
import com.strangegrotto.wealthdraft.assetimpls.stock.ImmStockAssetSnapshot;
import com.strangegrotto.wealthdraft.backend.assets.impl.ExpectedExampleAssetDefinitions;
import com.strangegrotto.wealthdraft.backend.assethistory.api.types.AssetSnapshot;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class ExpectedExampleAssetsHistory {
    public static final SortedMap<LocalDate, Map<String, AssetSnapshot<?>>> EXPECTED_ASSETS_HISTORY;

    static {
        var history_2020_11_30 = Map.<String, AssetSnapshot<?>>of(
                ExpectedExampleAssetDefinitions.BROKERAGE_ACCOUNT_ID, ImmStockAssetSnapshot.of(new BigDecimal(1200), new BigDecimal(90)),
                ExpectedExampleAssetDefinitions.BANK_ACCOUNT_ID, ImmBankAccountAssetSnapshot.of(new BigDecimal(12000), new BigDecimal("0.02")),
                ExpectedExampleAssetDefinitions.BITCOIN_HOLDING_ID, ImmBankAccountAssetSnapshot.of(new BigDecimal(15000), new BigDecimal("0.0"))
        );
        var history_2020_08_15 = Map.<String, AssetSnapshot<?>>of(
                ExpectedExampleAssetDefinitions.RETIREMENT_ACCOUNT_ID, ImmStockAssetSnapshot.of(new BigDecimal(17000), new BigDecimal(11))
        );
        var history_2020_06_01 = Map.<String, AssetSnapshot<?>>of(
                ExpectedExampleAssetDefinitions.RETIREMENT_ACCOUNT_ID, ImmStockAssetSnapshot.of(new BigDecimal(15000), new BigDecimal(10)),
                ExpectedExampleAssetDefinitions.BROKERAGE_ACCOUNT_ID, ImmStockAssetSnapshot.of(new BigDecimal(1000), new BigDecimal(100)),
                ExpectedExampleAssetDefinitions.BANK_ACCOUNT_ID, ImmBankAccountAssetSnapshot.of(new BigDecimal(4000), new BigDecimal("0.02")),
                ExpectedExampleAssetDefinitions.BITCOIN_HOLDING_ID, ImmBankAccountAssetSnapshot.of(new BigDecimal(13000), new BigDecimal("0.0"))
        );

        // ---------------------------------------------------------------------------------------------
        EXPECTED_ASSETS_HISTORY = Collections.unmodifiableSortedMap(new TreeMap<>(Map.of(
                LocalDate.of(2020, 11, 30), history_2020_11_30,
                LocalDate.of(2020, 8, 15), history_2020_08_15,
                LocalDate.of(2020, 6, 1), history_2020_06_01
        )));
    }
}
