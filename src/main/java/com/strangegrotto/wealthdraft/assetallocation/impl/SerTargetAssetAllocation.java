package com.strangegrotto.wealthdraft.assetallocation.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import com.strangegrotto.wealthdraft.assetallocation.api.types.TargetAssetAllocation;
import org.immutables.value.Value;

import java.math.BigDecimal;
import java.util.Optional;

@WealthdraftImmutableStyle
@Value.Immutable
@JsonDeserialize(as = ImmTargetAssetAllocation.class)
public interface SerTargetAssetAllocation extends TargetAssetAllocation {
    @Override
    @JsonProperty("numerator")
    String getNumeratorFilterId();

    @Override
    @JsonProperty("denominator")
    Optional<String> getDenominatorFilterId();

    // Fraction meaning 0.7, rather than 70%
    @Override
    @JsonProperty("fraction")
    BigDecimal getFraction();
}
