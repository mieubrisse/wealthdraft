package com.strangegrotto.wealthdraft.assetallocation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import com.strangegrotto.wealthdraft.assetallocation.filters.AssetFilter;
import org.immutables.value.Value;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@WealthdraftImmutableStyle
@Value.Immutable
@JsonDeserialize(as = ImmTargetAssetAllocation.class)
public interface TargetAssetAllocation {
    @JsonProperty("numeratorFilter")
    String getNumeratorFilter();

    // Fraction meaning 0.7, rather than 70%
    @JsonProperty("fraction")
    BigDecimal getFraction();

    // If not present, the denominator is assumed to be the entire portfolio
    @JsonProperty("denominatorFilter")
    Optional<String> getDenominatorFilterOpt();
}
