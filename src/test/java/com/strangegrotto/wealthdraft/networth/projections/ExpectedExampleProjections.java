package com.strangegrotto.wealthdraft.networth.projections;

import com.strangegrotto.wealthdraft.backend.assets.impl.ExpectedExampleAssetDefinitions;
import com.strangegrotto.wealthdraft.backend.projections.impl.SerProjectionScenario;
import com.strangegrotto.wealthdraft.backend.projections.api.types.AssetChange;
import com.strangegrotto.wealthdraft.backend.projections.impl.temporal.AssetParameterChangeValueOperation;
import com.strangegrotto.wealthdraft.assetimpls.bankaccount.ImmBankAccountAssetChange;
import com.strangegrotto.wealthdraft.backend.projections.impl.temporal.ImmSerAssetParameterChange;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;

/**
 * Encapsulates what data we expect the example projections file to have
 */
public class ExpectedExampleProjections {
    public static final String SELL_ALL_BTC_3Y_ID = "btc-3y";
    public static final String SELL_HALF_BTC_1Y_ID = "half-btc-1y";
    public static final String SELL_OTHER_HALF_BTC_2Y_ID = "half-btc-2y";

    public static final SerProjectionScenario SELL_ALL_BTC_3Y_SCENARIO;
    public static final SerProjectionScenario SELL_HALF_BTC_1Y_SCENARIO;
    public static final SerProjectionScenario SELL_OTHER_HALF_BTC_2Y_SCENARIO;

    public static final Map<String, SerProjectionScenario> EXPECTED_SCENARIOS;

    static {
        var today = LocalDate.now();

        // ----------------------- Sell all BTC in 3y scenario ----------------------------------------
        var allBtc3yBtcChange = ImmBankAccountAssetChange.builder()
                .balance(
                        ImmSerAssetParameterChange.of(new BigDecimal(0), AssetParameterChangeValueOperation.SET)
                )
                .build();
        var allBtc3yBankChange = ImmBankAccountAssetChange.builder()
                .balance(
                        ImmSerAssetParameterChange.of(new BigDecimal(15000), AssetParameterChangeValueOperation.ADD)
                )
                .build();
        var allBtc3yAllChanges = new TreeMap<LocalDate, Map<String, AssetChange>>(Map.of(
                today.plusYears(3), Map.of(
                        ExpectedExampleAssetDefinitions.BITCOIN_HOLDING_ID, allBtc3yBtcChange,
                        ExpectedExampleAssetDefinitions.BANK_ACCOUNT_ID, allBtc3yBankChange
                )
        ));
        SELL_ALL_BTC_3Y_SCENARIO = ImmProjectionScenario.of(
                "Sell Bitcoin in 3 years for 15k",
                allBtc3yAllChanges
        );

        // ----------------------- Sell half BTC in 1y scenario ----------------------------------------
        var halfBtc1yBtcChange = ImmBankAccountAssetChange.builder()
                .balance(
                        ImmSerAssetParameterChange.of(new BigDecimal(7500), AssetParameterChangeValueOperation.SUBTRACT)
                )
                .build();
        var halfBtc1yBankChange = ImmBankAccountAssetChange.builder()
                .balance(
                        ImmSerAssetParameterChange.of(new BigDecimal(7500), AssetParameterChangeValueOperation.ADD)
                )
                .build();
        var halfBtc1yAllChanges = new TreeMap<LocalDate, Map<String, AssetChange>>(Map.of(
                today.plusYears(1), Map.of(
                        ExpectedExampleAssetDefinitions.BITCOIN_HOLDING_ID, halfBtc1yBtcChange,
                        ExpectedExampleAssetDefinitions.BANK_ACCOUNT_ID, halfBtc1yBankChange
                )
        ));
        SELL_HALF_BTC_1Y_SCENARIO = ImmProjectionScenario.of(
                "Sell 50% Bitcoin in 1 year",
                halfBtc1yAllChanges
        );

        // ----------------------- Sell half BTC in 1y scenario ----------------------------------------
        var otherHalfBtc2yBtcChange = ImmBankAccountAssetChange.builder()
                .balance(
                        ImmSerAssetParameterChange.of(new BigDecimal(7500), AssetParameterChangeValueOperation.SUBTRACT)
                )
                .build();
        var otherHalfBtc2yBankChange = ImmBankAccountAssetChange.builder()
                .balance(
                        ImmSerAssetParameterChange.of(new BigDecimal(7500), AssetParameterChangeValueOperation.ADD)
                )
                .build();
        var otherHalfBtc2yAllChanges = new TreeMap<LocalDate, Map<String, AssetChange>>(Map.of(
                today.plusYears(2), Map.of(
                        ExpectedExampleAssetDefinitions.BITCOIN_HOLDING_ID, otherHalfBtc2yBtcChange,
                        ExpectedExampleAssetDefinitions.BANK_ACCOUNT_ID, otherHalfBtc2yBankChange
                )
        ));
        otherHalfBtc2yAllChanges.putAll(halfBtc1yAllChanges);
        SELL_OTHER_HALF_BTC_2Y_SCENARIO = ImmProjectionScenario.of(
                "After selling 50% Bitcoin in 1 year, sell other 50% in 2 years",
                otherHalfBtc2yAllChanges
        ).withBase(SELL_HALF_BTC_1Y_ID);

        // ---------------------------------------------------------------------------------------------
        EXPECTED_SCENARIOS = Map.of(
                    SELL_ALL_BTC_3Y_ID, SELL_ALL_BTC_3Y_SCENARIO,
                    SELL_HALF_BTC_1Y_ID, SELL_HALF_BTC_1Y_SCENARIO,
                    SELL_OTHER_HALF_BTC_2Y_ID, SELL_OTHER_HALF_BTC_2Y_SCENARIO
        );
    }
}
