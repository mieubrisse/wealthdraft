package com.strangegrotto.wealthdraft.tax;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.strangegrotto.wealthdraft.govconstants.ImmutableGovConstantsForYear;
import org.immutables.value.Value;

import java.util.Map;

@Value.Immutable
@JsonDeserialize(as = ImmutableScenarioTaxes.class)
public interface ScenarioTaxes {
    Map<Tax, Double> getAmtTaxes();

    Map<Tax, Double> getPrimarySystemIncomeTaxes();

    // NOTE: maybe want to split up into component taxes
    Map<Tax, Double> getFicaTaxes();

    /**
     * @return True if the tax under the primary tax system is >= tax under the AMT system, false otherwise
     */
    @Value.Default
    default boolean isPrimarySystemHigher() {
        Double totalPrimaryIncomeTax = getPrimarySystemIncomeTaxes().values().stream()
                .reduce(0D, (l, r) -> l + r);
        Double totalAmtIncomeTax = getAmtTaxes().values().stream()
                .reduce(0D, (l, r) -> l + r);
        return totalPrimaryIncomeTax >= totalAmtIncomeTax;
    }
}
