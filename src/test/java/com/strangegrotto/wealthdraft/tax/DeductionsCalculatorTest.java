package com.strangegrotto.wealthdraft.tax;

import com.strangegrotto.wealthdraft.scenarios.ImmutableIncomeStreams;
import com.strangegrotto.wealthdraft.scenarios.IncomeStreams;
import org.junit.Assert;
import org.junit.Test;

public class DeductionsCalculatorTest {
    @Test
    public void testApplyDeductions() {
        IncomeStreams input = ImmutableIncomeStreams.builder()
                .earnedIncome(50000)
                .nonPreferentialUnearnedIncome(50000)
                .preferentialUnearnedIncome(50000)
                .build();

        // Earned income should get deducted first
        IncomeStreams result1 = DeductionsCalculator.applyDeduction(input, 25000);
        Assert.assertEquals(25000, result1.getEarnedIncome());
        Assert.assertEquals(50000, result1.getNonPreferentialUnearnedIncome());
        Assert.assertEquals(50000, result1.getPreferentialUnearnedIncome());

        // Non-pref unearned income should get deducted next
        IncomeStreams result2 = DeductionsCalculator.applyDeduction(input, 75000);
        Assert.assertEquals(0, result2.getEarnedIncome());
        Assert.assertEquals(25000, result2.getNonPreferentialUnearnedIncome());
        Assert.assertEquals(50000, result2.getPreferentialUnearnedIncome());

        // Pref unearned income should get deducted last
        IncomeStreams result3 = DeductionsCalculator.applyDeduction(input, 125000);
        Assert.assertEquals(0, result3.getEarnedIncome());
        Assert.assertEquals(0, result3.getNonPreferentialUnearnedIncome());
        Assert.assertEquals(25000, result3.getPreferentialUnearnedIncome());
    }
}
