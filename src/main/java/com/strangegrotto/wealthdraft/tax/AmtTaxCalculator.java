package com.strangegrotto.wealthdraft.tax;

import com.google.common.collect.ImmutableMap;
import com.strangegrotto.wealthdraft.govconstants.AmtConstants;
import com.strangegrotto.wealthdraft.govconstants.GovConstantsForYear;
import com.strangegrotto.wealthdraft.scenarios.ImmutableIncomeStreams;
import com.strangegrotto.wealthdraft.scenarios.IncomeStreams;
import com.strangegrotto.wealthdraft.scenarios.TaxScenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class AmtTaxCalculator {
    private static final Logger log = LoggerFactory.getLogger(AmtTaxCalculator.class);

    private AmtTaxCalculator(){}

    public static Map<Tax, Double> calculateAmtTax(TaxScenario scenario, GovConstantsForYear govConstants) {
        AmtConstants amtConstants = govConstants.getAmtConstants();

        ImmutableMap.Builder<Tax, Double> result = ImmutableMap.builder();

        // Apply AMT adjustments as unearned income
        long totalAmtAdjustments = scenario.getAmtAdjustments().stream()
                .reduce(0L, (l, r) -> l + r);
        log.debug("Total AMT Adjustments: {}", totalAmtAdjustments);
        IncomeStreams grossIncomeStreams = scenario.getIncomeStreams();
        // TODO are all AMT adjustments unearned income??
        long grossNonPrefUnearnedIncome = grossIncomeStreams.getNonPreferentialUnearnedIncome() + totalAmtAdjustments;
        IncomeStreams taxableIncomeStreams = ImmutableIncomeStreams.builder()
                .earnedIncome(grossIncomeStreams.getEarnedIncome())
                .nonPreferentialUnearnedIncome(grossNonPrefUnearnedIncome)
                .preferentialUnearnedIncome(grossIncomeStreams.getPreferentialUnearnedIncome())
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
        long nonPrefUnearnedIncome = taxableIncomeStreams.getNonPreferentialUnearnedIncome();
        long prefUnearnedIncome = taxableIncomeStreams.getPreferentialUnearnedIncome();

        long nonPrefIncome = earnedIncome + nonPrefUnearnedIncome;
        long prefIncome = prefUnearnedIncome; // Is there a preferential earned income??
        double nonPreferentialTax = calculateNonPreferentialTax(
                taxableIncomeStreams.getTotal(),
                nonPrefIncome,
                earnedIncome,
                govConstants.getForeignIncomeConstants().getForeignEarnedIncomeExemption(),
                scenario.getFractionForeignEarnedIncome(),
                amtConstants);
        // TODO figure out how to calculate AMT foreign tax credit and subtract it
        result.put(Tax.FED_NON_PREF_INCOME, nonPreferentialTax);

        // Preferential income: these are "stacked" on top of non-pref income, so unfortunately they don't start at the absolute
        //  lowest rate
        ProgressiveTaxCalculator fedLtcgTaxCalculator = new ProgressiveTaxCalculator(govConstants.getFederalLtcgBrackets());
        double prefPlusNonPrefLtcgTax = fedLtcgTaxCalculator.calculateTax(nonPrefIncome + prefIncome);
        log.debug("Pref + nonpref LTCG tax: {}", prefPlusNonPrefLtcgTax);
        double nonPrefLtcgTax = fedLtcgTaxCalculator.calculateTax(nonPrefIncome);
        log.debug("Nonpref LTCG tax: {}", nonPrefLtcgTax);
        double prefIncomeTax = prefPlusNonPrefLtcgTax - nonPrefLtcgTax;
        result.put(Tax.FED_PREF_INCOME, prefIncomeTax);

        return result.build();
    }

    private static double calculateNonPreferentialTax(
            long totalTaxableIncome,
            long nonPrefIncome, // NOTE: TOTAL non-pref income, not just unearned!
            long earnedIncome,
            long foreignEarnedIncomeExclusion,
            double fractionForeignEarnedIncome,
            AmtConstants amtConstants) {
        // Get exemption, minus any phaseout
        // TODO when calculating the exemption phaseout, do I get to subtract my FEI???? I'm assuming no
        long amountOverPhaseout = Math.max(0, totalTaxableIncome - amtConstants.getExemptionPhaseoutFloor());
        long exemptionReduction = (long)((double)amountOverPhaseout * amtConstants.getExemptionPhaseoutRate());
        long amtExemption = Math.max(0, amtConstants.getExemption() - exemptionReduction);
        log.debug("AMT Exemption Allowed: {}", amtExemption);

        long excludedFEI = Math.min(
                foreignEarnedIncomeExclusion,
                (long)((double)earnedIncome * fractionForeignEarnedIncome)
        );
        long nonPrefNonExcludedFEIIncome = nonPrefIncome - excludedFEI;
        long nonPrefNonExcludedFEIIncomeLessExemption = Math.max(
                0,
                nonPrefNonExcludedFEIIncome - amtExemption
        );

        ProgressiveTaxCalculator taxCalculator = new ProgressiveTaxCalculator(amtConstants.getBrackets());
        double totalIncomeTax = taxCalculator.calculateTax(excludedFEI + nonPrefNonExcludedFEIIncomeLessExemption);
        log.debug("AMT tax on FEIE income + non-FEI income: {}", totalIncomeTax);
        double excludedIncomeTax = taxCalculator.calculateTax(excludedFEI);
        log.debug("AMT tax on FEIE income: {}", excludedIncomeTax);

        return totalIncomeTax - excludedIncomeTax;
    }
}
