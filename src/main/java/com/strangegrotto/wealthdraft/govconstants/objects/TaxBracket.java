package com.strangegrotto.wealthdraft.govconstants.objects;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.math.BigDecimal;

@Value.Immutable
@JsonDeserialize(as = ImmutableTaxBracket.class)
public interface TaxBracket {
    BigDecimal getFloor();
    Double getRate();
}
