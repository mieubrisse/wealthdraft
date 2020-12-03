package com.strangegrotto.wealthdraft.networth.projections;

import com.google.common.collect.Maps;
import com.strangegrotto.wealthdraft.tax.ImmutableScenarioTaxes;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class ProjNetWorthCalculatorTest {

    /**
     * Attempting to subtract more funds from an asset than exist should throw an error
     */
    @Test
    public void testOversubtractionError() {
        Map<String, Long> latestAssetValues = new HashMap<>() {{
            put("asset1", 100L);
        }};

        Projections projections = ImmutableProjections.builder()
                .defaultAnnualGrowth(0D)
                .putScenarios()

        ProjNetWorthCalculator calculator = new ProjNetWorthCalculator(5);
        calculator.calculateNetWorthProjections()
    }
}
