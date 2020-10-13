package com.strangegrotto.wealthdraft.tax;

import com.google.common.collect.ImmutableMap;
import com.strangegrotto.wealthdraft.govconstants.FicaConstants;
import com.strangegrotto.wealthdraft.govconstants.GovConstantsForYear;
import com.strangegrotto.wealthdraft.scenarios.IncomeStreams;
import com.strangegrotto.wealthdraft.scenarios.Scenario;

import java.util.Map;

public class FicaTaxCalculator {
    private FicaTaxCalculator(){}

    public static Map<Tax, Double> calculateFicaTax(Scenario scenario, GovConstantsForYear govConstants) {
        // FICA doesn't care about 401k and IRA contributions, so no deductions here :(
        // See: https://ttlc.intuit.com/community/retirement/discussion/are-contributions-to-401k-plans-subject-to-fica-and-medicare/00/30372
        IncomeStreams income = scenario.getIncomeStreams();
        FicaConstants ficaConstants = govConstants.getFicaConstants();
        long earnedIncome = income.getEarnedIncome();

        ImmutableMap.Builder<Tax, Double> result = ImmutableMap.builder();

        // Social security tax
        long socialSecurityTaxableAmount = Math.min(ficaConstants.getSocialSecurityWageCap(), earnedIncome);
        double socialSecurityTax = ficaConstants.getSocialSecurityRate() * (double)socialSecurityTaxableAmount;
        result.put(Tax.SOCIAL_SECURITY, socialSecurityTax);

        // Medicare tax
        double medicareBaseTax = ficaConstants.getMedicareBaseRate() * (double)earnedIncome;
        long medicareSurtaxableAmount = Math.max(0, earnedIncome - ficaConstants.getMedicareSurtaxFloor());
        double medicareSurtax = ficaConstants.getMedicareSurtaxExtraRate() * (double)medicareSurtaxableAmount;
        double medicareTax = medicareBaseTax + medicareSurtax;
        result.put(Tax.MEDICARE, medicareTax);

        // TODO NIIT actually isn't an employment tax, though it is Medicare
        // Net Investment Income Tax (aka Unearned Income Medicare Contribution Surtax)
        long investmentIncome = income.getShortTermCapGains() + income.getLongTermCapGains() + income.getOtherUnearnedIncome();
        long niitTaxableAmount = Math.max(0, investmentIncome - ficaConstants.getNetInvestmentIncomeFloor());
        double niitTax = ficaConstants.getNetInvestmentIncomeTaxRate() * (double)niitTaxableAmount;
        result.put(Tax.NIIT, niitTax);

        return result.build();
    }
}