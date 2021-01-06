package com.strangegrotto.wealthdraft.assetallocation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import org.immutables.value.Value;

import java.math.BigDecimal;
import java.util.Optional;

@WealthdraftImmutableStyle
@Value.Immutable
@JsonDeserialize(as = ImmTargetAssetAllocation.class)
public interface TargetAssetAllocation {
    @JsonProperty("numerator")
    AssetFilter getNumeratorFilter();

    // Fraction meaning 0.7, rather than 70%
    @JsonProperty("fraction")
    BigDecimal getFraction();

    // If not present, the denominator is assumed to be the entire portfolio
    @JsonProperty("denominator")
    Optional<ConjunctiveAssetTagFilter> getDenominatorOpt();
}
