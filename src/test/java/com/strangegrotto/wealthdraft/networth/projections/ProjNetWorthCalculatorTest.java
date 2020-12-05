package com.strangegrotto.wealthdraft.networth.projections;

import com.strangegrotto.wealthdraft.errors.ValOrGerr;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedList;
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

    @Test
    public void testUnrollingAssetChanges_correctOperation() {
        LocalDate today = LocalDate.now();
        String assetId = "asset";
        String scenarioId1 = "scenario1";
        String scenarioId2 = "scenario2";
        LocalDate todayPlus1Year = today.plusYears(1);

        AssetChange firstAssetChange = new AssetChange(100L, AssetChangeValueOperation.ADD);
        AssetChange secondAssetChange = new AssetChange(200L, AssetChangeValueOperation.ADD);
        ProjectionScenario scenario1 = ImmutableProjectionScenario.builder()
                .name("Scenario 1")
                .putChanges(todayPlus1Year.toString(), new HashMap<>() {{
                    put(assetId, firstAssetChange);
                }})
                .build();
        ProjectionScenario scenario2 = ImmutableProjectionScenario.builder()
                .name("Scenario 2")
                .base(scenarioId1)
                .putChanges(todayPlus1Year.toString(), new HashMap<>() {{
                    put(assetId, secondAssetChange);
                }})
                .build();

        Map<String, ProjectionScenario> allScenarios = new HashMap() {{
            put(scenarioId1, scenario1);
            put(scenarioId2, scenario2);
        }};

        ValOrGerr<ProjNetWorthCalculator.AssetChangesForScenario> assetChangesOrErr =
                ProjNetWorthCalculator.unrollAssetChanges(scenarioId2, allScenarios);

        Assert.assertFalse("Expected no error when unrolling dependency scenarios", assetChangesOrErr.hasGerr());
        ProjNetWorthCalculator.AssetChangesForScenario assetChanges = assetChangesOrErr.getVal();
        Assert.assertEquals(1, assetChanges.size());
        Assert.assertTrue(assetChanges.containsKey(todayPlus1Year));

        ProjNetWorthCalculator.AssetChangesForDate changesIn1Year = assetChanges.get(todayPlus1Year);
        Assert.assertEquals(1, changesIn1Year.size());
        LinkedList<AssetChange> changesForAssetIn1Year = changesIn1Year.get(assetId);
        Assert.assertEquals(2, changesForAssetIn1Year.size());

        // The ordering for changes on the same day from different scenarios should be:
        //  most upstream scenario changes first, then the downstream changes
        Assert.assertEquals(firstAssetChange, changesForAssetIn1Year.get(0));
        Assert.assertEquals(secondAssetChange, changesForAssetIn1Year.get(1));
    }

    @Test
    public void testUnrollingAssetChanges_errorOnCycles() {
        LocalDate today = LocalDate.now();
        String assetId = "asset";
        String scenarioId1 = "scenario1";
        String scenarioId2 = "scenario2";
        String scenarioId3 = "scenario3";

        ProjectionScenario scenario1 = ImmutableProjectionScenario.builder()
                .name("Scenario 1")
                .base(scenarioId3)
                .putChanges(today.plusYears(1).toString(), new HashMap<>() {{
                    put(assetId, new AssetChange(100L, AssetChangeValueOperation.ADD));
                }})
                .build();
        ProjectionScenario scenario2 = ImmutableProjectionScenario.builder()
                .name("Scenario 2")
                .base(scenarioId1)
                .putChanges(today.plusYears(2).toString(), new HashMap<>() {{
                    put(assetId, new AssetChange(100L, AssetChangeValueOperation.ADD));
                }})
                .build();
        ProjectionScenario scenario3 = ImmutableProjectionScenario.builder()
                .name("Scenario 2")
                .base(scenarioId2)
                .putChanges(today.plusYears(3).toString(), new HashMap<>() {{
                    put(assetId, new AssetChange(100L, AssetChangeValueOperation.ADD));
                }})
                .build();

        Map<String, ProjectionScenario> allScenarios = new HashMap() {{
            put(scenarioId1, scenario1);
            put(scenarioId2, scenario2);
            put(scenarioId3, scenario3);
        }};

        ValOrGerr<ProjNetWorthCalculator.AssetChangesForScenario> assetChangesOrErr =
                ProjNetWorthCalculator.unrollAssetChanges(scenarioId2, allScenarios);
        Assert.assertTrue("Expected an error due to the scenario cycle but no error was thrown", assetChangesOrErr.hasGerr());
    }
}
