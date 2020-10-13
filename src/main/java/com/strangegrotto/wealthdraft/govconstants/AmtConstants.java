package com.strangegrotto.wealthdraft.govconstants;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import com.strangegrotto.wealthdraft.validator.DeserializationValidator;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@JsonDeserialize(as = ImmutableAmtConstants.class)
public interface AmtConstants {
    long getExemption();
    long getExemptionPhaseoutFloor();
    double getExemptionPhaseoutRate();
    List<TaxBracket> getBrackets();

    @Value.Check
    default void check() {
        Preconditions.checkState(getExemption() > 0, "AMT exemption must be > 0");
        Preconditions.checkState(getExemptionPhaseoutFloor() > 0, "AMT exemption phaseout floor must be > 0");
        DeserializationValidator.checkIsRatio("AMT exemption phaseout rate", getExemptionPhaseoutRate());
        DeserializationValidator.checkUniqueBracketFloors("AMT tax brackets", getBrackets());
    }
}
