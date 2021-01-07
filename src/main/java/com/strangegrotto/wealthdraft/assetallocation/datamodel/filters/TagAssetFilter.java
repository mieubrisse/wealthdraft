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
@JsonDeserialize(as = ImmTagAssetFilter.class)
public abstract class TagAssetFilter implements AssetFilter {
    @JsonProperty("tag")
    public abstract String getTag();

    @JsonProperty("value")
    public abstract String getValue();

    @Override
    public final Set<String> apply(Map<String, Asset<?, ?>> allAssets, Set<String> currentSelection) {
        var tagName = getTag();
        var tagValue = getValue();
        return allAssets.entrySet().stream()
                .filter(entry -> {
                    var asset = entry.getValue();
                    var assetTags = asset.getTags();
                    return assetTags.containsKey(tagName) && tagValue.equals(assetTags.get(tagName));
                })
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }
}
