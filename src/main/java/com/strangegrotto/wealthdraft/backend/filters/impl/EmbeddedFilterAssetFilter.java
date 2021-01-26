package com.strangegrotto.wealthdraft.backend.filters.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import com.strangegrotto.wealthdraft.backend.assets.api.types.Asset;
import com.strangegrotto.wealthdraft.backend.filters.api.types.AssetFilter;
import com.strangegrotto.wealthdraft.backend.tags.custom.api.types.CustomTagDefinition;
import org.immutables.value.Value;

import java.util.*;

@WealthdraftImmutableStyle
@Value.Immutable
@JsonDeserialize(as = ImmEmbeddedFilterAssetFilter.class)
abstract class EmbeddedFilterAssetFilter implements SerAssetFilter {
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
    public void validate(Map<String, ValidatableAssetFilter> allFilters, Map<String, CustomTagDefinition> customTags) {
        var filterId = getFilterId();
        Preconditions.checkState(allFilters.containsKey(filterId));
    }

    @Override
    public Optional<List<String>> checkForCycles(Map<String, ValidatableAssetFilter> allFilters, LinkedHashSet<String> parentFilters) {
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
