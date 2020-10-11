package com.strangegrotto.wealthdraft.govconstants;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import com.strangegrotto.wealthdraft.validator.DeserializationValidator;
import org.immutables.value.Value;


@Value.Immutable
@JsonDeserialize(as = ImmutableFicaConstants.class)
public interface FicaConstants {
    long getSocialSecurityWageCap();
    double getSocialSecurityRate();
    double getMedicareBaseRate();
    long getMedicareSurtaxFloor();
    double getMedicareSurtaxExtraRate();

    // aka Unearned Income Medicare Contribution Surtax
    double getNetInvestmentIncomeTaxRate();
    int getNetInvestmentIncomeFloor();

    @Value.Check
    default void check() {
        Preconditions.checkState(getSocialSecurityWageCap() > 0, "Social Security wage cap must be > 0");
        DeserializationValidator.checkIsRatio("Social Security tax rate", getSocialSecurityRate());
        DeserializationValidator.checkIsRatio("Medicare base rate", getMedicareBaseRate());
        Preconditions.checkState(getMedicareSurtaxFloor() > 0, "Medicare surtax floor must be > 0");
        DeserializationValidator.checkIsRatio("Medicare extra surtax rate", getMedicareSurtaxExtraRate());
        DeserializationValidator.checkIsRatio("NIIT tax rate", getNetInvestmentIncomeTaxRate());
        Preconditions.checkState(getNetInvestmentIncomeFloor() > 0, "NIIT tax floor must be > 0");
    }
}
