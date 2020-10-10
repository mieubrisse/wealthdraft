package com.strangegrotto.wealthdraft.govconstants.objects;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.math.BigDecimal;

@Value.Immutable
@JsonDeserialize(as = ImmutableForeignIncomeConstants.class)
public interface ForeignIncomeConstants {
    BigDecimal getForeignEarnedIncomeExemption();
}
