package com.strangegrotto.wealthdraft.networth.history;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import com.strangegrotto.wealthdraft.assets.definition.Asset;
import com.strangegrotto.wealthdraft.assets.definition.AssetDefinitions;
import com.strangegrotto.wealthdraft.assets.temporal.AssetSnapshot;
import org.immutables.value.Value;

import java.time.LocalDate;
import java.util.Map;
import java.util.SortedMap;

@WealthdraftImmutableStyle
@Value.Immutable
public interface AssetsHistory {

    @Value.Parameter
    Map<String, Asset> getAssets();

    @Value.Parameter
    Map<String, SortedMap<LocalDate, AssetSnapshot>> getHistory();

    @Value.Check
    default void check() {
        LocalDate today = LocalDate.now();
        var assets = getAssets();
        var history = getHistory();

        // Verify that all assets in the history are in the asset list
        for (String assetId : history.keySet()) {
            Preconditions.checkState(
                    assets.containsKey(assetId),
                    "Asset '%s' appears in the history but isn't defined in the assets list",
                    assetId
            );

            for (LocalDate date : getHistory().get(assetId).keySet()) {
                Preconditions.checkState(
                        !date.isAfter(today),
                        "Asset '%s' has a historical record with date '%s', which is in the future",
                        assetId,
                        date
                );
            }
        }
    }
}
