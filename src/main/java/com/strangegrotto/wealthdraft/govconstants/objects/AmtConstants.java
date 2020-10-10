package com.strangegrotto.wealthdraft.govconstants.objects;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.math.BigDecimal;

@Value.Immutable
@JsonDeserialize(as = ImmutableAmtConstants.class)
public interface AmtConstants {
    BigDecimal getExemption();
    BigDecimal getExemptionPhaseoutFloor();
    Double getExemptionPhaseoutRate();
    Double getLowEarnerRate();
    Double getHighEarnerRate();
}
