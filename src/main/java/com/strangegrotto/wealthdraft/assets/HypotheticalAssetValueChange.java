package com.strangegrotto.wealthdraft.assets;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableHypotheticalAssetValueChange.class)
public interface HypotheticalAssetValueChange {
    // Date of the change
    String getDate();

    // Value change (can include absolute things like "0" as well as deltas like "-150" or "+150")
    String getValue();
}
