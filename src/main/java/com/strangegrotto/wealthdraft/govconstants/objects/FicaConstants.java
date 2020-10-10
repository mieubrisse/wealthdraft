package com.strangegrotto.wealthdraft.govconstants.objects;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.math.BigDecimal;

@Value.Immutable
@JsonDeserialize(as = ImmutableFicaConstants.class)
public interface FicaConstants {
    BigDecimal getSocialSecurityWageCap();
    Double getSocialSecurityRate();
    Double getMedicareBaseRate();
    BigDecimal getMedicareSurtaxFloor();
    Double getMedicareSurtaxExtraRate();

    // aka Unearned Income Medicare Contribution Surtax
    Double getNetInvestmentIncomeTaxRate();
    BigDecimal getNetInvestmentIncomeThreshold();
}
