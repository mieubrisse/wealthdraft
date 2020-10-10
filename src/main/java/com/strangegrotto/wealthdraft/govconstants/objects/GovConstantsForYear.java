package com.strangegrotto.wealthdraft.govconstants.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.math.BigDecimal;
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
    BigDecimal getStandardDeduction();
}
