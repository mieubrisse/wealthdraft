package com.strangegrotto.wealthdraft.assetallocation.datamodel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import com.strangegrotto.wealthdraft.assetfilters.AssetFilter;
import org.immutables.value.Value;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

@WealthdraftImmutableStyle
@Value.Immutable
// Deserialized using custom deserializer
public interface TargetAssetAllocations {
    // TODO add configurable WARN/ERROR thresholds

    @Value.Parameter
    Map<String, AssetFilter> getFilters();

    @Value.Parameter
    List<TargetAssetAllocation> getTargets();

    @Value.Check
    default void check() {
        var filters = getFilters();
        var targets = getTargets();

        for (var filterEntry : filters.entrySet()) {
            var filterName = filterEntry.getKey();
            var filter = filterEntry.getValue();

            var parentFilters = new LinkedHashSet<>(List.of(filterName));
            var cycleOpt = filter.checkForCycles(filters, parentFilters);
            if (cycleOpt.isPresent()) {
                throw new IllegalStateException(Strings.lenientFormat(
                        "Found an asset filter cycle: %s",
                        filterName,
                        String.join(" -> ", cycleOpt.get())
                ));
            }
        }

        for (int i = 0; i < targets.size(); i++) {
            var target = targets.get(i);

            var numeratorFilterName = target.getNumeratorFilter();
            Preconditions.checkState(
                    filters.containsKey(numeratorFilterName),
                    "Numerator filter '%s' in allocation target #%s is not defined",
                    numeratorFilterName,
                    i
            );

            var denominatorFilterNameOpt = target.getDenominatorFilterOpt();
            if (denominatorFilterNameOpt.isPresent()) {
                var denominatorFilterName = denominatorFilterNameOpt.get();
                Preconditions.checkState(
                        filters.containsKey(denominatorFilterName),
                        "Denominator filter '%s' in allocation target #%s is not defined",
                        denominatorFilterName,
                        i
                );
            }

            var targetFraction = target.getFraction();
            Preconditions.checkState(
                    targetFraction.compareTo(BigDecimal.ZERO) >= 0,
                    "Fraction for allocation target #%s is less than zero",
                    i);
            Preconditions.checkState(
                    targetFraction.compareTo(BigDecimal.ONE) <= 0,
                    "Fraction for allocation target #%s is greater than 1.0",
                    i);
        }
    }
}
