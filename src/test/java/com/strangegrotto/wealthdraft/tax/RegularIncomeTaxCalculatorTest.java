package com.strangegrotto.wealthdraft.tax;

import com.strangegrotto.wealthdraft.scenarios.ImmutableIncomeStreams;
import com.strangegrotto.wealthdraft.scenarios.IncomeStreams;
import org.junit.Assert;
import org.junit.Test;

public class RegularIncomeTaxCalculatorTest {
    @Test
    public void testApplyStdDeductionGivenFEI() {
        IncomeStreams input = ImmutableIncomeStreams.builder()
                .earnedIncome(50000)
                .nonPreferentialUnearnedIncome(50000)
                .preferentialUnearnedIncome(50000)
                .build();

        IncomeStreams result = RegularIncomeTaxCalculator.applyPostFEIEDeductions(input, 50000, 15000);
        // Earned income shouldn't be reduced since it's all FEI excluded
        Assert.assertEquals(50000, result.getEarnedIncome());

        // Non pref unearned income SHOULD be reduced by only the std deduction
        Assert.assertEquals(35000, result.getNonPreferentialUnearnedIncome());

        // Pref unearned income shouldn't be touched
        Assert.assertEquals(50000, result.getPreferentialUnearnedIncome());
    }
}
