package com.strangegrotto.wealthdraft.backend.assets.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.ImmutableMap;
import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import com.strangegrotto.wealthdraft.backend.assets.api.types.Asset;
import com.strangegrotto.wealthdraft.backend.assets.api.types.AssetType;
import com.strangegrotto.wealthdraft.backend.tagstores.intrinsic.IntrinsicAssetTag;
import org.immutables.value.Value;

import java.util.Map;
import java.util.stream.Collectors;

@WealthdraftImmutableStyle
@Value.Immutable
@JsonDeserialize(as = ImmSerAsset.class)
abstract class SerAsset implements Asset {
    public abstract String getName();

    public abstract AssetType getType();

    @JsonProperty("tags")
    abstract Map<String, String> getCustomTags();

    public final Map<String, String> getTags() {
        var intrinsicTagsAsStr = getIntrinsicTags().entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().getTagName(),
                        Map.Entry::getValue
                ));
        return ImmutableMap.<String, String>builder()
                .putAll(intrinsicTagsAsStr)
                .putAll(getCustomTags())
                .build();
    }

    // This function is the "registry" of intrinsic tags, so that it's concentrated in one place
    private final Map<IntrinsicAssetTag, String> getIntrinsicTags() {
        return Map.of(
                IntrinsicAssetTag.ASSET_TYPE, this.getType().name()
        );
    }
}
