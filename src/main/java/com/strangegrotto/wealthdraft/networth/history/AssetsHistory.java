package com.strangegrotto.wealthdraft.networth.history;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import com.strangegrotto.wealthdraft.assets.definition.Asset;
import com.strangegrotto.wealthdraft.assets.temporal.AssetSnapshot;
import org.immutables.value.Value;

import java.time.LocalDate;
import java.util.Map;
import java.util.SortedMap;

@WealthdraftImmutableStyle
@Value.Immutable
public interface AssetsHistory {

    @Value.Parameter
    Map<String, Asset<?, ?>> getAssets();

    @JsonProperty("history")
    @Value.Parameter
    SortedMap<LocalDate, Map<String, AssetSnapshot<?>>> getHistory();

    @Value.Check
    default void check() {
        LocalDate today = LocalDate.now();
        var assets = getAssets();
        var history = getHistory();

        // Verify that all assets in the history are in the asset list
        for (var historyEntry : history.entrySet()) {
            var date = historyEntry.getKey();
            var assetSnapshots = historyEntry.getValue();
            Preconditions.checkState(
                    !date.isAfter(today),
                    "Found a historical record with date '%s', which is in the future",
                    date
            );

            for (String assetId : assetSnapshots.keySet()) {
                Preconditions.checkState(
                        assets.containsKey(assetId),
                        "Asset '%s' appears in the history on '%s' but isn't defined in the assets list",
                        assetId,
                        date
                );
            }
        }
    }
}
