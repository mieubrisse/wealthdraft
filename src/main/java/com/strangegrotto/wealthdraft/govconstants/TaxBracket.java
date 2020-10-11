package com.strangegrotto.wealthdraft.govconstants;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import com.strangegrotto.wealthdraft.validator.DeserializationValidator;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableTaxBracket.class)
public interface TaxBracket {
    long getFloor();
    double getRate();

    @Value.Check
    default void check() {
        Preconditions.checkState(getFloor() >= 0, "Tax bracket floor must be >= 0");
        DeserializationValidator.checkIsRatio("tax bracket rate", getRate());
    }
}
