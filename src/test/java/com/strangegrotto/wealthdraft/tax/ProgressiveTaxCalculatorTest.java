package com.strangegrotto.wealthdraft.tax;

import com.google.common.collect.ImmutableList;
import com.strangegrotto.wealthdraft.govconstants.ImmutableTaxBracket;
import com.strangegrotto.wealthdraft.govconstants.TaxBracket;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class ProgressiveTaxCalculatorTest {

    @Test
    public void testCalculateTax_someTax() {
        // Rates are intentionally out-of-order
        List<TaxBracket> brackets = ImmutableList.of(
            ImmutableTaxBracket.builder()
                .floor(50_000)
                .rate(0.25)
                .build(),
            ImmutableTaxBracket.builder()
                .floor(100_000)
                .rate(0.5)
                .build(),
            ImmutableTaxBracket.builder()
                .floor(0)
                .rate(0.0)
                .build()
        );
        ProgressiveTaxCalculator calculator = new ProgressiveTaxCalculator(brackets);

        long gross = 150_000;
        double expectedTax = 37500;
        double actualTax = calculator.calculateTax(gross);
        Assert.assertEquals(expectedTax, actualTax, 0.0);
    }
}
