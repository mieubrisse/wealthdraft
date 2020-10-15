package com.strangegrotto.wealthdraft.tax;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.strangegrotto.wealthdraft.govconstants.GovConstantsForYear;
import com.strangegrotto.wealthdraft.scenarios.ImmutableIncomeStreams;
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
        log.debug("Deductions: {}", deductions);

        long retirementDeductions = deductions.getTrad401kDeduction() +
                deductions.getTradIraDeduction();
        IncomeStreams taxableIncome = DeductionsCalculator.applyDeduction(scenario.getIncomeStreams(), retirementDeductions);
        log.debug("Reg Fed Taxable Income (includes FEI, std deduc NOT applied): {}", taxableIncome);

        long excludedFEI = Math.min(
                govConstants.getForeignIncomeConstants().getForeignEarnedIncomeExemption(),
                (long)((double)taxableIncome.getEarnedIncome() * scenario.getFractionForeignEarnedIncome())
        );
        log.debug("Excluded FEI: {}", excludedFEI);
        taxableIncome = applyStdDeductionGivenFEI(
                taxableIncome,
                excludedFEI,
                govConstants.getStandardDeduction()
        );
        log.debug("Reg Fed Taxable Income (includes FEI, std deduc IS applied): {}", taxableIncome);

        long earnedIncome = taxableIncome.getEarnedIncome();
        long nonPrefUnearnedIncome = taxableIncome.getNonPreferentialUnearnedIncome();
        long prefUnearnedIncome = taxableIncome.getPreferentialUnearnedIncome();

        ImmutableMap.Builder<Tax, Double> totalTaxes = ImmutableMap.builder();

        // Earned income tax
        long nonPrefIncome = earnedIncome + nonPrefUnearnedIncome;
        long prefIncome = prefUnearnedIncome; // Is there even pref, earned income??

        ProgressiveTaxCalculator taxCalculator = new ProgressiveTaxCalculator(govConstants.getFederalIncomeTaxBrackets());
        double excludedFEITax = taxCalculator.calculateTax(excludedFEI);
        double nonPrefIncomeTax = taxCalculator.calculateTax(nonPrefIncome);
        double nonPrefTaxWithFEIE = nonPrefIncomeTax - excludedFEITax;
        totalTaxes.put(Tax.NON_PREFERENTIAL_INCOME, nonPrefTaxWithFEIE);

        // Preferential capital gains: these are "stacked" on top of other income, so unfortunately they don't start at the absolute
        //  lowest rate
        ProgressiveTaxCalculator fedLtcgTaxCalculator = new ProgressiveTaxCalculator(govConstants.getFederalLtcgBrackets());
        double prefPlusNonPrefLtcgTax = fedLtcgTaxCalculator.calculateTax(prefIncome + nonPrefIncome);
        double nonPrefLtcgTax = fedLtcgTaxCalculator.calculateTax(nonPrefIncome);
        double prefTax = prefPlusNonPrefLtcgTax - nonPrefLtcgTax;
        totalTaxes.put(Tax.PREFERENTIAL_INCOME, prefTax);

        return totalTaxes.build();
    }

    /**
     * We can't just use {@link DeductionsCalculator#applyDeduction} as-is with the standard deduction because
     * then it will potentially reduce dollars which should have been excluded under foreign-earned income.
     * @param income Income streams to reduce
     * @param excludedForeignEarnedIncome The amount of foreign-earned income that can actually be excluded
     * @param stdDeduction Standard deduction
     * @return Income streams with standard deduction applied (given the FEIE)
     */
    @VisibleForTesting
    static IncomeStreams applyStdDeductionGivenFEI(
            IncomeStreams income,
            // Of the foreign-earned income, how much is actually excluded based on FEIE and time spent abroad
            long excludedForeignEarnedIncome,
            long stdDeduction) {
        Preconditions.checkArgument(
                excludedForeignEarnedIncome <= income.getEarnedIncome(),
                "Excluded foreign-earned income cannot be greater than earned income!");

        // To apply the standard deduction, we need to subtract out the FEI and then add it back
        //  after the deduction is applied (so the std deduction doesn't erroneously reduce any FEI excluded
        //  dollars)
        IncomeStreams incomeLessFEI = ImmutableIncomeStreams.builder()
                .earnedIncome(income.getEarnedIncome() - excludedForeignEarnedIncome)
                .nonPreferentialUnearnedIncome(income.getNonPreferentialUnearnedIncome())
                .preferentialUnearnedIncome(income.getPreferentialUnearnedIncome())
                .build();
        IncomeStreams afterDeduction = DeductionsCalculator.applyDeduction(incomeLessFEI, stdDeduction);
        return ImmutableIncomeStreams.builder()
                .earnedIncome(afterDeduction.getEarnedIncome() + excludedForeignEarnedIncome)
                .nonPreferentialUnearnedIncome(afterDeduction.getNonPreferentialUnearnedIncome())
                .preferentialUnearnedIncome(afterDeduction.getPreferentialUnearnedIncome())
                .build();
    }
}
