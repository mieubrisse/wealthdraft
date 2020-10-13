package com.strangegrotto.wealthdraft.tax;

import com.google.common.collect.ImmutableMap;
import com.strangegrotto.wealthdraft.govconstants.AmtConstants;
import com.strangegrotto.wealthdraft.govconstants.GovConstantsForYear;
import com.strangegrotto.wealthdraft.scenarios.ImmutableIncomeStreams;
import com.strangegrotto.wealthdraft.scenarios.IncomeStreams;
import com.strangegrotto.wealthdraft.scenarios.Scenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class AmtTaxCalculator {
    private static final Logger log = LoggerFactory.getLogger(AmtTaxCalculator.class);

    private AmtTaxCalculator(){}

    public static Map<Tax, Double> calculateAmtTax(Scenario scenario, GovConstantsForYear govConstants) {
        AmtConstants amtConstants = govConstants.getAmtConstants();

        ImmutableMap.Builder<Tax, Double> result = ImmutableMap.builder();

        // Apply AMT adjustments as unearned income
        long totalAmtAdjustments = scenario.getAmtAdjustments().stream()
                .reduce(0L, (l, r) -> l + r);
        log.debug("Total AMT Adjustments: {}", totalAmtAdjustments);
        IncomeStreams grossIncomeStreams = scenario.getIncomeStreams();
        // TODO are all AMT adjustments unearned income??
        long amtUnearnedIncome = grossIncomeStreams.getOtherUnearnedIncome() + totalAmtAdjustments;
        IncomeStreams taxableIncomeStreams = ImmutableIncomeStreams.builder()
                .earnedIncome(grossIncomeStreams.getEarnedIncome())
                .otherUnearnedIncome(amtUnearnedIncome)
                .shortTermCapGains(grossIncomeStreams.getShortTermCapGains())
                .longTermCapGains(grossIncomeStreams.getLongTermCapGains())
                .build();

        // AMT allows trad IRA and trad 401k, but not standard deduction
        Deductions deductions = DeductionsCalculator.calculateAllowedDeductions(scenario, govConstants);
        long retirementDeductions = deductions.getTrad401kDeduction() +
                deductions.getTradIraDeduction();
        log.debug("Retirement Deductions: {}", retirementDeductions);
        taxableIncomeStreams = DeductionsCalculator.applyDeduction(
                taxableIncomeStreams,
                retirementDeductions);
        log.debug("AMT Taxable Income (includes FEI): {}", taxableIncomeStreams);

        long earnedIncome = taxableIncomeStreams.getEarnedIncome();
        long otherUnearnedIncome = taxableIncomeStreams.getOtherUnearnedIncome();
        long stcg = taxableIncomeStreams.getShortTermCapGains();
        long ltcg = taxableIncomeStreams.getLongTermCapGains();


        long nonPreferentialIncome = earnedIncome + otherUnearnedIncome + stcg;
        double nonPreferentialTax = calculateNonPreferentialTax(
                taxableIncomeStreams.getTotal(),
                nonPreferentialIncome,
                earnedIncome,
                govConstants.getForeignIncomeConstants().getForeignEarnedIncomeExemption(),
                scenario.getFractionForeignEarnedIncome(),
                amtConstants);
        // TODO subtract AMT foreign tax credit (whatever that is)
        result.put(Tax.NON_PREFERENTIAL_INCOME, nonPreferentialTax);

        // Preferential cap gains: these are "stacked" on top of existing income, so unfortunately they don't start at the absolute
        //  lowest rate
        ProgressiveTaxCalculator fedLtcgTaxCalculator = new ProgressiveTaxCalculator(govConstants.getFederalLtcgBrackets());
        double ltcgPlusNonPrefIncomeLtcgTax = fedLtcgTaxCalculator.calculateTax(nonPreferentialIncome + ltcg);
        double nonPrefIncomeLtcgTax = fedLtcgTaxCalculator.calculateTax(nonPreferentialIncome);
        double ltcgTax = ltcgPlusNonPrefIncomeLtcgTax - nonPrefIncomeLtcgTax;
        result.put(Tax.PREFERENTIAL_INCOME, ltcgTax);

        return result.build();
    }

    private static double calculateNonPreferentialTax(
            long totalTaxableIncome,
            long nonPreferentialIncome,
            long earnedIncome,
            long foreignEarnedIncomeExclusion,
            double fractionForeignEarnedIncome,
            AmtConstants amtConstants) {
        // Get exemption, minus any phaseout
        // TODO when calculating the exemption phaseout, do I get to subtract my FEI???? I'm assuming no
        long amountOverPhaseout = Math.max(0, totalTaxableIncome - amtConstants.getExemptionPhaseoutFloor());
        long exemptionReduction = (long)((double)amountOverPhaseout * amtConstants.getExemptionPhaseoutRate());
        long amtExemption = amtConstants.getExemption() - exemptionReduction;
        log.debug("AMT Exemption Allowed: {}", amtExemption);

        long excludedFEI = Math.min(
                foreignEarnedIncomeExclusion,
                (long)((double)earnedIncome * fractionForeignEarnedIncome)
        );
        long nonPreferentialNonExcludedFEIIncome = nonPreferentialIncome - excludedFEI;
        long nonPreferentialNonExcludedFEIIncomeLessExemption = Math.max(
                0,
                nonPreferentialNonExcludedFEIIncome - amtExemption
        );

        ProgressiveTaxCalculator taxCalculator = new ProgressiveTaxCalculator(amtConstants.getBrackets());
        double excludedIncomeTax = taxCalculator.calculateTax(excludedFEI);
        double totalIncomeTax = taxCalculator.calculateTax(excludedFEI + nonPreferentialNonExcludedFEIIncomeLessExemption);

        return totalIncomeTax - excludedIncomeTax;
    }
}
