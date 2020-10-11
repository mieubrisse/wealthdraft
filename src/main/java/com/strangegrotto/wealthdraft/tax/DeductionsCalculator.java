package com.strangegrotto.wealthdraft.tax;

import com.google.common.collect.ImmutableList;
import com.strangegrotto.wealthdraft.ImmutableIncomeStreams;
import com.strangegrotto.wealthdraft.govconstants.GovConstantsForYear;
import com.strangegrotto.wealthdraft.govconstants.RetirementConstants;
import com.strangegrotto.wealthdraft.scenarios.IncomeStreams;
import com.strangegrotto.wealthdraft.scenarios.Scenario;

import java.util.ArrayList;
import java.util.List;

public class DeductionsCalculator {
    private DeductionsCalculator(){}

    /**
     * Examines the particular scenario and calculates exactly how much of each deduction is allowed
     */
    public static Deductions calculateAllowedDeductions(Scenario scenario, GovConstantsForYear govConstants) {
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
        double deductionMultiplier = phaseoutRangeFillPct == 0 ? 1.0 : 1 / phaseoutRangeFillPct;
        long tradIraDeduction = (long) (deductionMultiplier * scenario.getIraContrib().getTrad());

        return ImmutableDeductions.builder()
                .trad401kDeduction(reg401kContrib)
                .tradIraDeduction(tradIraDeduction)
                // TODO reduce the standard deduction in various scenarios
                .standardDeduction(govConstants.getStandardDeduction())
                .build();
    }

    /**
     * Reduces the given income streams by the given deduction, applying it in the correct order
     * @param income Income streams to reduce
     * @param deduction Deduction amount to apply
     * @return A new {@link IncomeStreams} object with income reduced by the deduction
     */
    public static IncomeStreams applyDeduction(IncomeStreams income, long deduction) {
        // Deductions get applied to earned income first, and only after to unearned income (which is good)
        // See: https://www.kitces.com/blog/long-term-capital-gains-bump-zone-higher-marginal-tax-rate-phase-in-0-rate
        List<Long> incomesToReduceInOrder = ImmutableList.of(
                income.getEarnedIncome(),
                income.getOtherUnearnedIncome(),
                income.getShortTermCapGains(),
                income.getLongTermCapGains());
        List<Long> resultingReducedIncomes = new ArrayList<>();
        long remainingDeduction = deduction;
        for (Long incomeToReduce : incomesToReduceInOrder) {
            long resultingIncome = incomeToReduce;
            if (remainingDeduction > 0) {
                long amountToReduce = Math.min(remainingDeduction, incomeToReduce);
                resultingIncome = incomeToReduce - amountToReduce;
                remainingDeduction -= amountToReduce;
            }
            resultingReducedIncomes.add(resultingIncome);
        }

        return ImmutableIncomeStreams.builder()
                // TODO we can do better than referencing list indexes, but it's fine for now
                .earnedIncome(resultingReducedIncomes.get(0))
                .otherUnearnedIncome(resultingReducedIncomes.get(1))
                .shortTermCapGains(resultingReducedIncomes.get(2))
                .longTermCapGains(resultingReducedIncomes.get(3))
                .build();
    }

}
