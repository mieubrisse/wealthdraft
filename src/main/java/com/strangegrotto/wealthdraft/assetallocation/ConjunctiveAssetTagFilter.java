package com.strangegrotto.wealthdraft.assetallocation;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import com.strangegrotto.wealthdraft.assets.definition.Asset;
import org.immutables.value.Value;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@WealthdraftImmutableStyle
@Value.Immutable
@JsonDeserialize(as = ImmConjunctiveAssetTagFilter.class)
public abstract class ConjunctiveAssetTagFilter implements AssetFilter {
    @Value.Parameter
    public abstract Map<String, String> getTags();

    @Override
    public Map<String, Asset<?, ?>> apply(Map<String, Asset<?, ?>> input) {
        var result = new HashMap<String, Asset<?, ?>>();
        var selectors = this.getTags();
        input.forEach((assetId, asset) -> {
            var assetTags = asset.getTags();
            var matches = true;
            for (var selectorEntry : selectors.entrySet()) {
                var selectorName = selectorEntry.getKey();
                var selectorValue = selectorEntry.getValue();
                matches = matches && assetTags.containsKey(selectorName)
                        && selectorValue.equals(assetTags.get(selectorName));
            }
            if (matches) {
                result.put(assetId, asset);
            }
        });
        return result;
    }

    @Override
    public String getStringRepresentation() {
        var selectorsList = getTags().entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.toList());
        return String.join("\n", selectorsList);
    }
}
