package com.strangegrotto.wealthdraft.backend.assetallocation.calculator;

import com.strangegrotto.wealthdraft.backend.assetallocationcalc.api.types.AssetAllocationDeviationStatus;
import com.strangegrotto.wealthdraft.backend.assetallocationcalc.impl.SimpleAssetAllocationCalculator;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

public class TargetAssetAllocationsStoreTest {
    @Test
    public void testCalculationMath() {
        var currentNumerator = BigDecimal.valueOf(50_000);
        var currentDenominator = BigDecimal.valueOf(300_000);
        var targetFraction = BigDecimal.valueOf(0.10);
        var result = SimpleAssetAllocationCalculator.calcSingleAssetAllocation(
                currentNumerator,
                currentDenominator,
                targetFraction,
                BigDecimal.valueOf(0.10),
                BigDecimal.valueOf(0.20)
        );

        var expectedCurrentFraction = currentNumerator.divide(
                currentDenominator,
                SimpleAssetAllocationCalculator.DIVISION_SCALE,
                SimpleAssetAllocationCalculator.ROUNDING_MODE);
        var expectedTargetNumeratorValue = BigDecimal.valueOf(30_000);
        var expectedCorrectionNeeded = BigDecimal.valueOf(-20_000);
        var expectedDeviationFraction = expectedCorrectionNeeded
                .abs()
                .divide(
                        expectedTargetNumeratorValue,
                        SimpleAssetAllocationCalculator.DIVISION_SCALE,
                        SimpleAssetAllocationCalculator.ROUNDING_MODE
                );

        // We use .compareTo rather than .equals because .equals compares value AND scale
        Assert.assertEquals(0, result.getCurrentFraction().compareTo(expectedCurrentFraction));
        Assert.assertEquals(0, result.getTargetNumeratorValue().compareTo(expectedTargetNumeratorValue));
        Assert.assertEquals(0, result.getCorrectionNeeded().compareTo(expectedCorrectionNeeded));
        Assert.assertEquals(0, result.getDeviationFraction().compareTo(expectedDeviationFraction));
    }

    @Test
    public void testOkDeviationStatus() {
        // Lower
        var lower = SimpleAssetAllocationCalculator.calcSingleAssetAllocation(
                BigDecimal.valueOf(28_000),
                BigDecimal.valueOf(300_000),
                BigDecimal.valueOf(0.10),
                BigDecimal.valueOf(0.10),
                BigDecimal.valueOf(0.20)
        );
        Assert.assertEquals(AssetAllocationDeviationStatus.OK, lower.getDeviationStatus());

        // Higher
        var higher = SimpleAssetAllocationCalculator.calcSingleAssetAllocation(
                BigDecimal.valueOf(32_000),
                BigDecimal.valueOf(300_000),
                BigDecimal.valueOf(0.10),
                BigDecimal.valueOf(0.10),
                BigDecimal.valueOf(0.20)
        );
        Assert.assertEquals(AssetAllocationDeviationStatus.OK, higher.getDeviationStatus());
    }

    @Test
    public void testWarnDeviationStatus() {
        // Lower
        var lower = SimpleAssetAllocationCalculator.calcSingleAssetAllocation(
                BigDecimal.valueOf(26_000),
                BigDecimal.valueOf(300_000),
                BigDecimal.valueOf(0.10),
                BigDecimal.valueOf(0.10),
                BigDecimal.valueOf(0.20)
        );
        Assert.assertEquals(AssetAllocationDeviationStatus.WARN, lower.getDeviationStatus());

        // Higher
        var higher = SimpleAssetAllocationCalculator.calcSingleAssetAllocation(
                BigDecimal.valueOf(34_000),
                BigDecimal.valueOf(300_000),
                BigDecimal.valueOf(0.10),
                BigDecimal.valueOf(0.10),
                BigDecimal.valueOf(0.20)
        );
        Assert.assertEquals(AssetAllocationDeviationStatus.WARN, higher.getDeviationStatus());
    }

    @Test
    public void testErrDeviationStatus() {
        // Lower
        var lower = SimpleAssetAllocationCalculator.calcSingleAssetAllocation(
                BigDecimal.valueOf(22_000),
                BigDecimal.valueOf(300_000),
                BigDecimal.valueOf(0.10),
                BigDecimal.valueOf(0.10),
                BigDecimal.valueOf(0.20)
        );
        Assert.assertEquals(AssetAllocationDeviationStatus.ERROR, lower.getDeviationStatus());

        // Higher
        var higher = SimpleAssetAllocationCalculator.calcSingleAssetAllocation(
                BigDecimal.valueOf(38_000),
                BigDecimal.valueOf(300_000),
                BigDecimal.valueOf(0.10),
                BigDecimal.valueOf(0.10),
                BigDecimal.valueOf(0.20)
        );
        Assert.assertEquals(AssetAllocationDeviationStatus.ERROR, higher.getDeviationStatus());
    }

    /*
    public testNormalOperation() throws IOException  {
        var deviationFractionWarn = BigDecimal.valueOf(0.4);
        var deviationFractionErr = BigDecimal.valueOf(0.9);
        var assetAllocationCalculator = new AssetAllocationCalculator(deviationFractionWarn, deviationFractionErr);

        var mapper = Main.getObjectMapper();
        var assetDefinitionsExampleUrl = AssetDefinitionsTestFiles.EXAMPLE.getResource();
        var assetDefinitions = mapper.readValue(assetDefinitionsExampleUrl, AssetDefinitions.class);

        var assetHistoryExampleUrl = AssetsHistoryTestFiles.EXAMPLE.getResource();
        var assetsHistory = mapper.readValue(assetHistoryExampleUrl, AssetsHistory.class);
        var assetsHistoryByDate = assetsHistory.getHistoryByDate();
        var latestDate = assetsHistoryByDate.lastKey();
        var latestSnapshots = assetsHistoryByDate.get(latestDate);

        var targetAllocationsExampleUrl = TargetAssetAllocationsTestFiles.EXAMPLE.getResource();
        var targetAllocations = mapper.readValue(targetAllocationsExampleUrl, TargetAssetAllocations.class);

        var results = assetAllocationCalculator.calculate(
                targetAllocations,
                assetDefinitions.getAssets(),
                latestSnapshots
        );

        var expectedTotalPortfolioValue = BigDecimal.valueOf(322_000);
        var expectedDomesticAssetsValue = BigDecimal.valueOf(307_000);
        var expectedIntlAssetsValue = BigDecimal.valueOf(15_000);

        Assert.assertEquals(3, results.size());
        var calcResult1 = results.get(0);
        Assert.assertTrue(expectedDomesticAssetsValue.compareTo(calcResult1.getCurrentNumeratorValue()));

        var latestSnapshots = Map.of(
                assetId1, ImmBankAccountAssetSnapshot.
        )
        var mapper = Main.getObjectMapper();
        var assetsHistory = ExpectedExampleAssetsHistory.EXPECTED_ASSETS_HISTORY;
        var historyByDate = assetsHistory.
    }

     */
}
