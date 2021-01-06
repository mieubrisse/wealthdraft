package com.strangegrotto.wealthdraft.assetallocation.filters;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import com.strangegrotto.wealthdraft.assets.definition.Asset;
import org.immutables.value.Value;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
public interface AssetFilter {
    Set<String> apply(Map<String, Asset<?, ?>> allAssets, Set<String> currentSelection);
}
