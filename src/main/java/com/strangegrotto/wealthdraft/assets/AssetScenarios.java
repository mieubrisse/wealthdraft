package com.strangegrotto.wealthdraft.assets;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.util.List;
import java.util.Map;

@Value.Immutable
@JsonDeserialize(as = ImmutableAssetScenarios.class)
public interface AssetScenarios {
    // Map<String, String> getScenarios();

    Map<String, Asset> getAssets();
}
