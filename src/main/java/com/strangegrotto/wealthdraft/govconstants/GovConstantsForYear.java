package com.strangegrotto.wealthdraft.govconstants;

import java.util.List;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import com.strangegrotto.wealthdraft.validator.DeserializationValidator;

@Value.Immutable
@JsonDeserialize(as = ImmutableGovConstantsForYear.class)
public interface GovConstantsForYear {
    @JsonProperty("amt") AmtConstants getAmtConstants();
    List<TaxBracket> getFederalIncomeTaxBrackets();
    List<TaxBracket> getFederalLtcgBrackets();
    @JsonProperty("fica") FicaConstants getFicaConstants();
    @JsonProperty("foreignIncome") ForeignIncomeConstants getForeignIncomeConstants();
    @JsonProperty("retirement") RetirementConstants getRetirementConstants();
    @JsonProperty("hsaContribLimit") long getHsaContributionLimit();
    long getStandardDeduction();

    @Value.Check
    default void check() {
        DeserializationValidator.checkUniqueBracketFloors("federal income brackets", getFederalIncomeTaxBrackets());
        DeserializationValidator.checkUniqueBracketFloors("federal longterm cap gains brackets", getFederalLtcgBrackets());
        Preconditions.checkState(getStandardDeduction() > 0, "Standard deduction must be > 0");
    }
}
