package com.strangegrotto.wealthdraft.assetallocation.datamodel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import com.strangegrotto.wealthdraft.assetallocation.datamodel.filters.AssetFilter;
import org.immutables.value.Value;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@WealthdraftImmutableStyle
@Value.Immutable
@JsonDeserialize(as = ImmTargetAssetAllocations.class)
public interface TargetAssetAllocations {
    @Value.Parameter
    @JsonProperty("filters")
    Map<String, AssetFilter> getFilters();

    @Value.Parameter
    @JsonProperty("targets")
    List<TargetAssetAllocation> getTargets();

    @Value.Check
    default void check() {
        var filters = getFilters();
        var targets = getTargets();
        for (int i = 0; i < targets.size(); i++) {
            var target = targets.get(i);

            var numeratorFilterName = target.getNumeratorFilter();
            Preconditions.checkState(
                    filters.containsKey(numeratorFilterName),
                    "Numerator filter '%s' in allocation target #%i is not defined",
                    numeratorFilterName,
                    i
            );

            var denominatorFilterNameOpt = target.getDenominatorFilterOpt();
            if (denominatorFilterNameOpt.isPresent()) {
                var denominatorFilterName = denominatorFilterNameOpt.get();
                Preconditions.checkState(
                        filters.containsKey(denominatorFilterName),
                        "Denominator filter '%s' in allocation target #%i is not defined",
                        denominatorFilterName,
                        i
                );
            }

            var targetFraction = target.getFraction();
            Preconditions.checkState(
                    targetFraction.compareTo(BigDecimal.ZERO) >= 0,
                    "Fraction for allocation target #%i is less than zero",
                    i);
            Preconditions.checkState(
                    targetFraction.compareTo(BigDecimal.ONE) <= 0,
                    "Fraction for allocation target #%i is greater than 1.0",
                    i);
        }
    }
}
