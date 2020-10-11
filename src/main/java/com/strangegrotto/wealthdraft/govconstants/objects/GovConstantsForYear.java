package com.strangegrotto.wealthdraft.govconstants.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@JsonDeserialize(as = ImmutableGovConstantsForYear.class)
public interface GovConstantsForYear {
    int getYear();
    @JsonProperty("amt") AmtConstants getAmtConstants();
    List<TaxBracket> getFederalIncomeTaxBrackets();
    List<TaxBracket> getFederalLtcgBrackets();
    @JsonProperty("fica") FicaConstants getFicaConstants();
    @JsonProperty("foreignIncome") ForeignIncomeConstants getForeignIncomeConstants();
    @JsonProperty("retirement") RetirementConstants getRetirementConstants();
    int getStandardDeduction();

    @Value.Check
    default void check() {
        Preconditions.checkState(getYear() > 1900 && getYear() < 2200, "Year must be in range [1990, 2200]");
        Preconditions.checkState(getStandardDeduction() > 0, "Standard deduction must be > 0");
    }
}
