package com.strangegrotto.wealthdraft.govconstants.objects;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.math.BigDecimal;

@Value.Immutable
@JsonDeserialize(as = ImmutableRetirementConstants.class)
public interface RetirementConstants {
    BigDecimal getPersonal401kContribLimit();
    BigDecimal getTotal401kContribLimit();
    BigDecimal getIraContribLimit();
    BigDecimal getTradIraDeductiblePhaseoutFloor();
    BigDecimal getTradIraDeductiblePhaseoutCeiling();
}
