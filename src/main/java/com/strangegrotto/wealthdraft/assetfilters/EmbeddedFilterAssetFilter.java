package com.strangegrotto.wealthdraft.assetfilters;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import com.strangegrotto.wealthdraft.assets.definition.Asset;
import org.immutables.value.Value;

import java.util.*;

@WealthdraftImmutableStyle
@Value.Immutable
@JsonDeserialize(as = ImmEmbeddedFilterAssetFilter.class)
public abstract class EmbeddedFilterAssetFilter implements AssetFilter {
    @JsonProperty("filter")
    public abstract String getFilterId();

    @Override
    public Map<String, Asset> apply(Map<String, AssetFilter> allFilters, Map<String, Asset> input) {
        var embeddedFilterName = getFilterId();
        Preconditions.checkState(
                allFilters.containsKey(embeddedFilterName),
                "Could not apply embedded filter; no filter found with name '%s'",
                embeddedFilterName);

        var embeddedFilter = allFilters.get(embeddedFilterName);
        return embeddedFilter.apply(allFilters, input);
    }

    @Override
    public Optional<List<String>> checkForCycles(Map<String, AssetFilter> allFilters, LinkedHashSet<String> parentFilters) {
        var embeddedFilterName = getFilterId();
        Preconditions.checkState(
                allFilters.containsKey(embeddedFilterName),
                "Could not check for cycles; no filter found with name '%s'",
                embeddedFilterName
        );

        if (parentFilters.contains(embeddedFilterName)) {
            var cycle = new ArrayList<>(parentFilters);
            cycle.add(embeddedFilterName);
            return Optional.of(cycle);
        }

        var newParentFilters = Sets.newLinkedHashSet(parentFilters);
        newParentFilters.add(embeddedFilterName);

        var embeddedFilter = allFilters.get(embeddedFilterName);
        return embeddedFilter.checkForCycles(allFilters, newParentFilters);
    }
}
