package com.strangegrotto.wealthdraft.tax;

import com.google.common.collect.ImmutableList;
import com.strangegrotto.wealthdraft.govconstants.GovConstantsForYear;
import com.strangegrotto.wealthdraft.govconstants.RetirementConstants;
import com.strangegrotto.wealthdraft.scenarios.HsaContrib;
import com.strangegrotto.wealthdraft.scenarios.ImmutableIncomeStreams;
import com.strangegrotto.wealthdraft.scenarios.IncomeStreams;
import com.strangegrotto.wealthdraft.scenarios.TaxScenario;

import java.util.List;
import java.util.function.Function;

public class DeductionsCalculator {
    private DeductionsCalculator(){}

    /**
     * Examines the particular scenario and calculates exactly how much of each deduction is allowed
     */
    public static Deductions calculateAllowedDeductions(TaxScenario scenario, GovConstantsForYear govConstants) {
        long grossTotalIncome = scenario.getIncomeStreams().getTotal();
        long reg401kContrib = scenario.get401kContrib().getTrad();

        // MAGI (which DOESN'T show up on the 1040) is sometimes defined as "AGI, with certain deductions like
        // IRA contrib added back in". If we sue this way, we get a circular dependency: you need IRA contrib to calculate
        //  AGI to calculate MAGI to determine whether you can include IRA contributions:
        //  https://money.stackexchange.com/questions/94585/circular-dependency-involving-ira-deduction
        // Instead, we do things in the logical order and find MAGI *first*
        // See also : https://www.investopedia.com/terms/m/magi.asp
        long modifiedAdjustedGrossIncome = grossTotalIncome - reg401kContrib; // TODO implement HSA deduction here

        // Figure out how much the trad IRA deduction reduces by (if anything)
        RetirementConstants retirementConstants = govConstants.getRetirementConstants();
        long phaseoutRangeFill = Math.max(
                0L,
                modifiedAdjustedGrossIncome - retirementConstants.getTradIraDeductiblePhaseoutFloor());
        long phaseoutRangeWidth = retirementConstants.getTradIraDeductiblePhaseoutCeiling() - retirementConstants.getTradIraDeductiblePhaseoutFloor();
        double phaseoutRangeFillPct = Math.min(1.0, (double)phaseoutRangeFill / (double)phaseoutRangeWidth);
        double deductionMultiplier = phaseoutRangeFillPct == 0 ? 1.0 : 1.0 - phaseoutRangeFillPct;
        long tradIraDeduction = (long) (deductionMultiplier * scenario.getIraContrib().getTrad());

        HsaContrib hsaContrib = scenario.getHsaContrib();
        long totalHsaContrib = hsaContrib.getViaPayroll() + hsaContrib.getViaOtherMethods();

        return ImmutableDeductions.builder()
                .trad401kDeduction(reg401kContrib)
                .tradIraDeduction(tradIraDeduction)
                // TODO reduce the standard deduction in various scenarios
                .standardDeduction(govConstants.getStandardDeduction())
                .hsaDeduction(totalHsaContrib)
                .build();
    }

    private static class IncomeToDeductionBuilder {
        long income;
        Function<Long, ImmutableIncomeStreams.Builder> builderFunc;

        IncomeToDeductionBuilder(
                long income,
                Function<Long, ImmutableIncomeStreams.Builder> builderFunc) {
            this.income = income;
            this.builderFunc = builderFunc;
        }
    }

    /**
     * Reduces the given income streams by the given deduction, applying it in the correct order
     * @param income Income streams to reduce
     * @param deduction Deduction amount to apply
     * @return A new {@link IncomeStreams} object with income reduced by the deduction
     */
    public static IncomeStreams applyDeduction(IncomeStreams income, long deduction) {
        // Deductions get applied in the following order:
        //  1. Earned income
        //  2. Non-preferential unearned income
        //  3. Preferential unearned income
        // See: https://www.kitces.com/blog/long-term-capital-gains-bump-zone-higher-marginal-tax-rate-phase-in-0-rate
        ImmutableIncomeStreams.Builder resultBuilder = ImmutableIncomeStreams.builder();
        List<IncomeToDeductionBuilder> incomesToReduceInOrder = ImmutableList.of(
                new IncomeToDeductionBuilder(
                        income.getEarnedIncome(),
                        resultBuilder::earnedIncome
                ),
                new IncomeToDeductionBuilder(
                        income.getNonPreferentialUnearnedIncome(),
                        resultBuilder::nonPreferentialUnearnedIncome
                ),
                new IncomeToDeductionBuilder(
                        income.getPreferentialUnearnedIncome(),
                        resultBuilder::preferentialUnearnedIncome
                )
        );
        long remainingDeduction = deduction;
        for (IncomeToDeductionBuilder pair : incomesToReduceInOrder) {
            Function<Long, ImmutableIncomeStreams.Builder> builderFunc = pair.builderFunc;
            long grossAmountToReduce = pair.income;
            long resultingIncome = grossAmountToReduce;
            if (remainingDeduction > 0) {
                long actualReduction = Math.min(remainingDeduction, grossAmountToReduce);
                resultingIncome = grossAmountToReduce - actualReduction;
                remainingDeduction -= actualReduction;
            }
            pair.builderFunc.apply(resultingIncome);
        }

        return resultBuilder.build();
    }

}
