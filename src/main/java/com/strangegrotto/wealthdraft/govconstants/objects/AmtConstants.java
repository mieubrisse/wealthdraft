package com.strangegrotto.wealthdraft.govconstants.objects;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import com.strangegrotto.wealthdraft.validator.DeserializationValidator;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableAmtConstants.class)
public interface AmtConstants {
    int getExemption();
    int getExemptionPhaseoutFloor();
    double getExemptionPhaseoutRate();
    double getLowEarnerRate();
    double getHighEarnerRate();

    @Value.Check
    default void check() {
        Preconditions.checkState(getExemption() > 0, "AMT exemption must be > 0");
        Preconditions.checkState(getExemptionPhaseoutFloor() > 0, "AMT exemption phaseout floor must be > 0");
        DeserializationValidator.checkIsRatio("AMT exemption phaseout rate", getExemptionPhaseoutRate());
        DeserializationValidator.checkIsRatio("AMT low earner rate", getLowEarnerRate());
        DeserializationValidator.checkIsRatio("AMT high earner rate", getHighEarnerRate());
    }
}
