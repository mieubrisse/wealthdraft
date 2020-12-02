package com.strangegrotto.wealthdraft.tax;

import com.strangegrotto.wealthdraft.govconstants.GovConstantsForYear;
import com.strangegrotto.wealthdraft.scenarios.Scenario;

import java.util.Map;

public class ScenarioTaxCalculator {
    private ScenarioTaxCalculator() {}

    public static ScenarioTaxes calculateScenarioTax(Scenario scenario, GovConstantsForYear govConstants) {
        Map<Tax, Double> primarySystemIncomeTaxes = RegularIncomeTaxCalculator.calculateRegularIncomeTax(scenario, govConstants);
        Map<Tax, Double> amtTaxes = AmtTaxCalculator.calculateAmtTax(scenario, govConstants);
        Map<Tax, Double> ficaTaxes = FicaTaxCalculator.calculateFicaTax(scenario, govConstants);

        return ImmutableScenarioTaxes.builder()
                .primarySystemIncomeTaxes(primarySystemIncomeTaxes)
                .amtTaxes(amtTaxes)
                .ficaTaxes(ficaTaxes)
                .build();
    }
}
