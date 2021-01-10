package com.strangegrotto.wealthdraft.assetallocation.datamodel.filters;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import com.strangegrotto.wealthdraft.assets.definition.Asset;
import org.immutables.value.Value;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@WealthdraftImmutableStyle
@Value.Immutable
@JsonDeserialize(as = ImmNegationAssetFilter.class)
public abstract class NegationAssetFilter implements AssetFilter {
    @JsonProperty("not")
    public abstract AssetFilter getNegated();

    @Override
    public Set<String> apply(Map<String, Asset> allAssets, Set<String> currentSelection) {
        var filtered = this.getNegated().apply(allAssets, currentSelection);
        return allAssets.keySet().stream()
                .filter(assetId -> !filtered.contains(assetId))
                .collect(Collectors.toSet());
    }

}
