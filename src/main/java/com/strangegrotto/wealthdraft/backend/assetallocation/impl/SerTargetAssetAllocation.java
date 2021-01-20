package com.strangegrotto.wealthdraft.backend.assetallocation.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import com.strangegrotto.wealthdraft.backend.assetallocation.api.types.TargetAssetAllocation;
import org.immutables.value.Value;

import java.math.BigDecimal;
import java.util.Optional;

@WealthdraftImmutableStyle
@Value.Immutable
@JsonDeserialize(as = ImmSerTargetAssetAllocation.class)
interface SerTargetAssetAllocation extends TargetAssetAllocation {
    @Override
    @JsonProperty("numerator")
    String getNumeratorFilterId();

    @Override
    @JsonProperty("denominator")
    Optional<String> getDenominatorFilterIdOpt();

    // Fraction meaning 0.7, rather than 70%
    @Override
    @JsonProperty("fraction")
    BigDecimal getFraction();
}
