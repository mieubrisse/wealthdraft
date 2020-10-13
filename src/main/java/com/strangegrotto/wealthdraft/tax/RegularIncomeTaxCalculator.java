package com.strangegrotto.wealthdraft.tax;

import com.google.common.collect.ImmutableMap;
import com.strangegrotto.wealthdraft.govconstants.GovConstantsForYear;
import com.strangegrotto.wealthdraft.scenarios.IncomeStreams;
import com.strangegrotto.wealthdraft.scenarios.Scenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class RegularIncomeTaxCalculator {
    private static final Logger log = LoggerFactory.getLogger(RegularIncomeTaxCalculator.class);

    private RegularIncomeTaxCalculator(){}

    /**
     * Calculates the taxes under the regular income tax system
     * @param scenario Scenario to calculate regular taxes for
     * @param govConstants Gov't constants to use in the calculation
     * @return A map of tax type -> tax amount under the regular income tax system
     */
    public static Map<Tax, Double> calculateRegularIncomeTax(Scenario scenario, GovConstantsForYear govConstants) {
        // Regular income tax allows trad IRA, trad 401k, and standard deduction
        // We don't apply the standard deduction yet though; that gets added later
        Deductions deductions = DeductionsCalculator.calculateAllowedDeductions(scenario, govConstants);
        long retirementDeductions = deductions.getTrad401kDeduction() +
                deductions.getTradIraDeduction();
        log.debug("Trad 401k & IRA deductions: {}", retirementDeductions);
        log.debug("Standard Deduction: {}", deductions.getStandardDeduction());

        IncomeStreams taxableIncome = DeductionsCalculator.applyDeduction(scenario.getIncomeStreams(), retirementDeductions);
        log.debug("Reg Fed Taxable Income (includes FEI, lacks standard deduction): {}", taxableIncome);

        long earnedIncome = taxableIncome.getEarnedIncome();
        long otherUnearnedIncome = taxableIncome.getOtherUnearnedIncome();
        long stcg = taxableIncome.getShortTermCapGains();
        long ltcg = taxableIncome.getLongTermCapGains();

        ImmutableMap.Builder<Tax, Double> totalTaxes = ImmutableMap.builder();

        // Earned income tax
        long nonPreferentialIncome = earnedIncome + otherUnearnedIncome + stcg;
        long excludedFEI = Math.min(
                govConstants.getForeignIncomeConstants().getForeignEarnedIncomeExemption(),
                (long)((double)earnedIncome * scenario.getFractionForeignEarnedIncome())
        );
        long nonPreferentialNonExcludedFEIIncome = nonPreferentialIncome - excludedFEI;

        // We apply the standard deduction separately here, rather than at the start, because the standard deduction
        //  gets applied AFTER the FEI exclusion. If we applied the standard deduction first, we'd potentially be
        //  "wasting" it on FEI that was going to get excluded anyways
        long stdDeductionUsedOnNonPrefNonFEIEIncome = Math.min(
                deductions.getStandardDeduction(),
                nonPreferentialNonExcludedFEIIncome
        );
        long nonPrefNonFEIETaxableIncome = Math.max(
                0,
                nonPreferentialNonExcludedFEIIncome - stdDeductionUsedOnNonPrefNonFEIEIncome
        );
        long remainingStdDeduction = deductions.getStandardDeduction() - stdDeductionUsedOnNonPrefNonFEIEIncome;

        ProgressiveTaxCalculator taxCalculator = new ProgressiveTaxCalculator(govConstants.getFederalIncomeTaxBrackets());
        long nonPreferentialTaxableIncome = excludedFEI + nonPrefNonFEIETaxableIncome;
        double excludedFEITax = taxCalculator.calculateTax(excludedFEI);
        double nonPrefIncomeWithExcludedFEITax = taxCalculator.calculateTax(nonPreferentialTaxableIncome);
        double nonPrefTax = nonPrefIncomeWithExcludedFEITax - excludedFEITax;
        totalTaxes.put(Tax.NON_PREFERENTIAL_INCOME, nonPrefTax);

        // Preferential capital gains: these are "stacked" on top of other income, so unfortunately they don't start at the absolute
        //  lowest rate
        ProgressiveTaxCalculator fedLtcgTaxCalculator = new ProgressiveTaxCalculator(govConstants.getFederalLtcgBrackets());
        long preferentialTaxableIncome = Math.max(
                0,
                ltcg - remainingStdDeduction
        );
        double ltcgPlusIncomeUnderneathTax = fedLtcgTaxCalculator.calculateTax(nonPreferentialTaxableIncome + preferentialTaxableIncome);
        double incomeUnderneathLtcgTax = fedLtcgTaxCalculator.calculateTax(nonPreferentialTaxableIncome);
        double ltcgTax = ltcgPlusIncomeUnderneathTax - incomeUnderneathLtcgTax;
        totalTaxes.put(Tax.PREFERENTIAL_INCOME, ltcgTax);

        return totalTaxes.build();

    }
}
