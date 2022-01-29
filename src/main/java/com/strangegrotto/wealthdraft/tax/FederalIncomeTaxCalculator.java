package com.strangegrotto.wealthdraft.tax;

import com.strangegrotto.wealthdraft.errors.ValOrGerr;
import com.strangegrotto.wealthdraft.govconstants.TaxBracket;
import com.strangegrotto.wealthdraft.scenarios.IncomeStreams;

import java.util.List;
import java.util.Map;

public class FederalIncomeTaxCalculator {
    // Calculates the non-preferential (STCG, earned income) and preferential (LTCG, qualified dividends) taxes, taking
    //  into account:
    // 1. Earned income-only deductions (e.g. trad 401k & trad IRA contributions)
    // 2. Foreign earned income exclusion
    // 3. Post-FEIE deductions (e.g. standard deduction & HSA deduction, which need to get applied AFTER FEIE is applied
    //     because if they're applied beforehand they're meaningless)
    private static ValOrGerr<Map<Tax, Double>> calculateFederalIncomeTax(
            IncomeStreams grossIncome,
            // What percentage of of gross earned income is foreign-earned
            double fractionForeignEarnedIncome,
            long foreignEarnedIncomeExclusionLimit,
            Deductions deductions,
            List<TaxBracket> taxBrackets,
    ) {
        long grossEarnedIncome = grossIncome.getEarnedIncome();

        // 401k contributions don't even show up on a W2
        long trad401kDeductions = deductions.getTrad401kDeduction();
        long post401kDeductionIncome = Math.max(
                0L,
                grossEarnedIncome - trad401kDeductions
        );

        long foreignEarnedIncome = (long)((double)post401kDeductionIncome * fractionForeignEarnedIncome);
        long excludableForeignEarnedIncome = Math.min(
                foreignEarnedIncomeExclusionLimit,
                foreignEarnedIncome
        );
        // The amount that would be eligible for IRA contributions
        long nonExcludableIncome = post401kDeductionIncome - excludableForeignEarnedIncome;
        if (deductions.getTradIraDeduction() > nonExcludableIncome) {
            // We should have validation protecting against this case anyways, but just in case
            return ValOrGerr.newGerr("The trad IRA deduction cannot be > gross_earned_income - 401k_deduction - foreign_excluded_income, but " +
                    "the trad IRA deduction was '{}' and the limit was '{}'",
                    deductions.getTradIraDeduction(),
                    nonExcludableIncome
            );
        }

        long post401kAndIraDeductionIncome = excludableForeignEarnedIncome + (nonExcludableIncome - deductions.getTradIraDeduction());

        // Now, calculate the tax assuming no foreign excluded income, and with foreign-excluded income








        long earnedIncomeAfter

        long tradIraContrib = deductions.getTradIraDeduction();


    }
}
