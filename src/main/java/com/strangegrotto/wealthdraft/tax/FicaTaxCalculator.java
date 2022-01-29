package com.strangegrotto.wealthdraft.tax;

import com.google.common.collect.ImmutableMap;
import com.strangegrotto.wealthdraft.govconstants.FicaConstants;
import com.strangegrotto.wealthdraft.govconstants.GovConstantsForYear;
import com.strangegrotto.wealthdraft.scenarios.IncomeStreams;
import com.strangegrotto.wealthdraft.scenarios.TaxScenario;

import java.util.Map;

public class FicaTaxCalculator {
    private FicaTaxCalculator(){}

    public static Map<Tax, Double> calculateFicaTax(TaxScenario scenario, GovConstantsForYear govConstants) {
        // FICA doesn't allow trad 401k and IRA deductions, nor foreign-earned income exclusion :(
        // See: https://ttlc.intuit.com/community/retirement/discussion/are-contributions-to-401k-plans-subject-to-fica-and-medicare/00/30372
        // However, it DOES allow a deduction for HSA contribution made exclusively through employer payroll
        long hsaContribViaPayrollDeduction = scenario.getHsaContrib().getViaPayroll();

        IncomeStreams income = scenario.getIncomeStreams();
        IncomeStreams taxableIncome = DeductionsCalculator.applyDeduction(income, hsaContribViaPayrollDeduction);
        long taxableEarnedIncome = taxableIncome.getEarnedIncome();

        FicaConstants ficaConstants = govConstants.getFicaConstants();
        ImmutableMap.Builder<Tax, Double> result = ImmutableMap.builder();

        // Social security tax
        long socialSecurityTaxableAmount = Math.min(ficaConstants.getSocialSecurityWageCap(), taxableEarnedIncome);
        double socialSecurityTax = ficaConstants.getSocialSecurityRate() * (double)socialSecurityTaxableAmount;
        result.put(Tax.SOCIAL_SECURITY, socialSecurityTax);

        // Medicare tax
        double medicareBaseTax = ficaConstants.getMedicareBaseRate() * (double)taxableEarnedIncome;
        long medicareSurtaxableAmount = Math.max(0, taxableEarnedIncome - ficaConstants.getMedicareSurtaxFloor());
        double medicareSurtax = ficaConstants.getMedicareSurtaxExtraRate() * (double)medicareSurtaxableAmount;
        double medicareTax = medicareBaseTax + medicareSurtax;
        result.put(Tax.MEDICARE, medicareTax);

        // TODO NIIT actually isn't an employment tax, though it is Medicare (really, Obamacare)
        // Net Investment Income Tax is calculated on the lesser of:
        // 1) the net investment income or
        // 2) the amount of net investment income that MAGI goes over the 200k threshold
        // This happens because unearned income stacks on top of earned income, so only investment income would be
        //  over the 200k threshold
        // Because we need MAGI, we need to calculate retirement deductions
        Deductions deductions = DeductionsCalculator.calculateAllowedDeductions(scenario, govConstants);
        long retirementDeductions = deductions.getTrad401kDeduction() + deductions.getTradIraDeduction();
        IncomeStreams incomeStreamsLessRetirementDeductions = DeductionsCalculator.applyDeduction(income, retirementDeductions);
        long investmentIncome = incomeStreamsLessRetirementDeductions.getNonPreferentialUnearnedIncome() +
                incomeStreamsLessRetirementDeductions.getPreferentialUnearnedIncome();
        long totalIncomeLessRetirementDeductions = incomeStreamsLessRetirementDeductions.getTotal();
        long niitTaxableAmount = Math.min(
                investmentIncome,
                Math.max(
                        0,
                        totalIncomeLessRetirementDeductions - ficaConstants.getNetInvestmentIncomeFloor()
                )
        );
        double niitTax = ficaConstants.getNetInvestmentIncomeTaxRate() * (double)niitTaxableAmount;
        result.put(Tax.NIIT, niitTax);

        return result.build();
    }
}
