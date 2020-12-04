package com.strangegrotto.wealthdraft.networth.projections;

import com.strangegrotto.wealthdraft.errors.ValOrGerr;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

public class ProjNetWorthCalculatorTest {

    /**
     * Attempting to subtract more funds from an asset than exist should throw an error
     */
    @Test
    public void testOversubtractionError() {
        String assetId = "asset1";
        long assetValue = 100L;
        Map<String, Long> latestAssetValues = new HashMap<>() {{
            put(assetId, assetValue);
        }};

        String scenarioId = "scenario1";
        ProjectionScenario scenario = ImmutableProjectionScenario.builder()
                .name("Test scenario")
                .putChanges("+1y", new HashMap<>() {{
                    // Wil
                    put(assetId, new AssetChange(10 * assetValue, AssetChangeValueOperation.SUBTRACT));
                }})
                .build();

        Projections projections = ImmutableProjections.builder()
                .defaultAnnualGrowth(0D)
                .putScenarios(scenarioId, scenario)
                .build();

        ProjNetWorthCalculator calculator = new ProjNetWorthCalculator(5);
        ProjNetWorthCalcResults calcResults = calculator.calculateNetWorthProjections(latestAssetValues, projections);
        ValOrGerr<SortedMap<LocalDate, Long>> netWorthProjsOrErr = calcResults.getProjNetWorths().get(scenarioId);
        Assert.assertTrue("Expected an error due to subtracting more value from asset than was available", netWorthProjsOrErr.hasGerr());
    }

    @Test
    public void testPastChangeDateError() {
        String assetId = "asset1";
        Map<String, Long> latestAssetValues = new HashMap<>() {{
            put(assetId, 100L);
        }};

        LocalDate tenDaysAgo = LocalDate.now().minusDays(10);

        String scenarioId = "scenario1";
        ProjectionScenario scenario = ImmutableProjectionScenario.builder()
                .name("Test scenario")
                .putChanges(tenDaysAgo.toString(), new HashMap<>() {{
                    // Wil
                    put(assetId, new AssetChange(100L, AssetChangeValueOperation.ADD));
                }})
                .build();

        Projections projections = ImmutableProjections.builder()
                .defaultAnnualGrowth(0D)
                .putScenarios(scenarioId, scenario)
                .build();

        ProjNetWorthCalculator calculator = new ProjNetWorthCalculator(5);
        ProjNetWorthCalcResults calcResults = calculator.calculateNetWorthProjections(latestAssetValues, projections);
        ValOrGerr<SortedMap<LocalDate, Long>> netWorthProjsOrErr = calcResults.getProjNetWorths().get(scenarioId);
        Assert.assertTrue("Expected an error due to a scenario with an asset change date in the past", netWorthProjsOrErr.hasGerr());
    }

    @Test
    public void testTodayChangeDateNoError() {
        String assetId = "asset1";
        Map<String, Long> latestAssetValues = new HashMap<>() {{
            put(assetId, 100L);
        }};

        String scenarioId = "scenario1";
        ProjectionScenario scenario = ImmutableProjectionScenario.builder()
                .name("Test scenario")
                .putChanges(LocalDate.now().toString(), new HashMap<>() {{
                    // Wil
                    put(assetId, new AssetChange(100L, AssetChangeValueOperation.ADD));
                }})
                .build();

        Projections projections = ImmutableProjections.builder()
                .defaultAnnualGrowth(0D)
                .putScenarios(scenarioId, scenario)
                .build();

        ProjNetWorthCalculator calculator = new ProjNetWorthCalculator(5);
        ProjNetWorthCalcResults calcResults = calculator.calculateNetWorthProjections(latestAssetValues, projections);
        ValOrGerr<SortedMap<LocalDate, Long>> netWorthProjsOrErr = calcResults.getProjNetWorths().get(scenarioId);
        Assert.assertFalse("Expected no error with a scenario with an asset change date of today", netWorthProjsOrErr.hasGerr());
    }
}
