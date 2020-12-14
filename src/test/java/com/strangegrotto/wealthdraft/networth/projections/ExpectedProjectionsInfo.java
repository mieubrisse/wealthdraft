package com.strangegrotto.wealthdraft.networth.projections;

import com.strangegrotto.wealthdraft.networth.BankAccountAssetChange;
import com.strangegrotto.wealthdraft.networth.ExpectedAssetsWithHistoryInfo;
import com.strangegrotto.wealthdraft.networth.ImmutableBankAccountAssetChange;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Encapsulates what data we expect the example projections file to have
 */
public class ExpectedProjectionsInfo {
    public static final String SELL_ALL_BTC_3Y_ID = "btc-3y";
    public static final String SELL_HALF_BTC_1Y_ID = "half-btc-1y";
    public static final String SELL_OTHER_HALF_BTC_2Y_ID = "half-btc-2y";

    public static final ProjectionScenario SELL_ALL_BTC_3Y_SCENARIO;
    public static final ProjectionScenario SELL_HALF_BTC_1Y_SCENARIO;
    public static final ProjectionScenario SELL_OTHER_HALF_BTC_2Y_SCENARIO;

    public static final Map<String, ProjectionScenario> EXPECTED_SCENARIOS;

    static {
        var today = LocalDate.now();

        // ----------------------- Sell all BTC in 3y scenario ----------------------------------------
        var allBtc3yBtcChange = ImmutableBankAccountAssetChange.builder()
                .balance(ImmutableAssetParameterChange.builder()
                        .operation(AssetParameterChangeValueOperation.SET)
                        .value(new BigDecimal(0))
                        .build()
                )
                .build();
        var allBtc3yBankChange = ImmutableBankAccountAssetChange.builder()
                .balance(ImmutableAssetParameterChange.builder()
                        .operation(AssetParameterChangeValueOperation.ADD)
                        .value(new BigDecimal(15000))
                        .build()
                )
                .build();
        var allBtc3yAllChanges = new TreeMap<LocalDate, Map<String, AssetChange>>(Map.of(
                today.plusYears(3), Map.of(
                        ExpectedAssetsWithHistoryInfo.BITCOIN_ACCOUNT_ASSET_ID, allBtc3yBtcChange,
                        ExpectedAssetsWithHistoryInfo.BANK_ACCOUNT_ASSET_ID, allBtc3yBankChange
                )
        ));
        SELL_ALL_BTC_3Y_SCENARIO = ImmutableProjectionScenario.builder()
                .name("Sell Bitcoin in 3 years for 15k")
                .base(Optional.empty())
                .assetChanges(allBtc3yAllChanges)
                .build();

        // ----------------------- Sell half BTC in 1y scenario ----------------------------------------
        var halfBtc1yBtcChange = ImmutableBankAccountAssetChange.builder()
                .balance(ImmutableAssetParameterChange.builder()
                        .operation(AssetParameterChangeValueOperation.SUBTRACT)
                        .value(new BigDecimal(7500))
                        .build()
                )
                .build();
        var halfBtc1yBankChange = ImmutableBankAccountAssetChange.builder()
                .balance(ImmutableAssetParameterChange.builder()
                        .operation(AssetParameterChangeValueOperation.ADD)
                        .value(new BigDecimal(7500))
                        .build()
                )
                .build();
        var halfBtc1yAllChanges = new TreeMap<LocalDate, Map<String, AssetChange>>(Map.of(
                today.plusYears(1), Map.of(
                        ExpectedAssetsWithHistoryInfo.BITCOIN_ACCOUNT_ASSET_ID, halfBtc1yBtcChange,
                        ExpectedAssetsWithHistoryInfo.BANK_ACCOUNT_ASSET_ID, halfBtc1yBankChange
                )
        ));
        SELL_HALF_BTC_1Y_SCENARIO = ImmutableProjectionScenario.builder()
                .name("Sell 50% Bitcoin in 1 year, then the other 50% a year later")
                .base(Optional.empty())
                .assetChanges(halfBtc1yAllChanges)
                .build();

        // ----------------------- Sell half BTC in 1y scenario ----------------------------------------
        var otherHalfBtc2yBtcChange = ImmutableBankAccountAssetChange.builder()
                .balance(ImmutableAssetParameterChange.builder()
                        .operation(AssetParameterChangeValueOperation.SUBTRACT)
                        .value(new BigDecimal(7500))
                        .build()
                )
                .build();
        var otherHalfBtc2yBankChange = ImmutableBankAccountAssetChange.builder()
                .balance(ImmutableAssetParameterChange.builder()
                        .operation(AssetParameterChangeValueOperation.ADD)
                        .value(new BigDecimal(7500))
                        .build()
                )
                .build();
        var otherHalfBtc2yAllChanges = new TreeMap<LocalDate, Map<String, AssetChange>>(Map.of(
                today.plusYears(2), Map.of(
                        ExpectedAssetsWithHistoryInfo.BITCOIN_ACCOUNT_ASSET_ID, otherHalfBtc2yBtcChange,
                        ExpectedAssetsWithHistoryInfo.BANK_ACCOUNT_ASSET_ID, otherHalfBtc2yBankChange
                )
        ));
        otherHalfBtc2yAllChanges.putAll(halfBtc1yAllChanges);
        SELL_OTHER_HALF_BTC_2Y_SCENARIO = ImmutableProjectionScenario.builder()
                .name("After selling 50% Bitcoin in 1 year, sell other 50% in 2 years")
                .base(Optional.of(SELL_HALF_BTC_1Y_ID))
                .assetChanges(otherHalfBtc2yAllChanges)
                .build();

        // ---------------------------------------------------------------------------------------------
        EXPECTED_SCENARIOS = Map.of(
                    SELL_ALL_BTC_3Y_ID, SELL_ALL_BTC_3Y_SCENARIO,
                    SELL_HALF_BTC_1Y_ID, SELL_HALF_BTC_1Y_SCENARIO,
                    SELL_OTHER_HALF_BTC_2Y_ID, SELL_OTHER_HALF_BTC_2Y_SCENARIO
        );
    }
}
