package com.strangegrotto.wealthdraft.networth.history;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import com.strangegrotto.wealthdraft.assets.definition.Asset;
import com.strangegrotto.wealthdraft.assets.temporal.AssetSnapshot;
import org.immutables.value.Value;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@WealthdraftImmutableStyle
@Value.Immutable
public interface AssetsHistory {

    @Value.Parameter
    Map<String, Asset<?, ?>> getAssets();

    // TODO Remove this entirely in favor of the date-first method
    @JsonProperty("history")
    @Value.Parameter
    Map<String, SortedMap<LocalDate, AssetSnapshot<?>>> getHistoryByAsset();

    /*
    This function repackages the results of getRawHistory to fix two problems:

    1. The users enter things in asset -> (date -> snapshot) form, but things are most useful to us in
        date -> (asset -> snapshot) form
    2. History can be declared piecemeal (e.g. asset A and B are declared on date T, asset B and C
        are declared on date T+1), but we need a snapshot at every time T that has all assets
     */
    @Value.Derived
    default SortedMap<LocalDate, Map<String, AssetSnapshot<?>>> getHistoryByDate() {
        var assetSnapshotsByDate = new TreeMap<LocalDate, Map<String, AssetSnapshot<?>>>();
        for (var assetHistoryEntry : getHistoryByAsset().entrySet()) {
            var assetId = assetHistoryEntry.getKey();
            var historyForAsset = assetHistoryEntry.getValue();
            for (var recordForAssetEntry : historyForAsset.entrySet()) {
                var date = recordForAssetEntry.getKey();
                var assetSnapshot = recordForAssetEntry.getValue();
                var snapshotsOnDate = assetSnapshotsByDate.getOrDefault(date, new HashMap<>());
                snapshotsOnDate.put(assetId, assetSnapshot);
                assetSnapshotsByDate.put(date, snapshotsOnDate);
            }
        }

        // Because history can be declared piecemeal (e.g. asset A and B are declared on date T, asset B and C
        //  are declared on date T+1) we "fill forward" past snapshots into any slots in the future where
        //  they're missing. This assumes that asset values don't change over historical time.
        var latestAssetSnapshots = new HashMap<String, AssetSnapshot<?>>();
        var result = new TreeMap<LocalDate, Map<String, AssetSnapshot<?>>>();
        for (var entry : assetSnapshotsByDate.entrySet()) {
            var date = entry.getKey();
            var assetSnapshotsForDate = entry.getValue();
            latestAssetSnapshots.putAll(assetSnapshotsForDate);

            var resultAssetSnapshotsForDate = result.getOrDefault(date, new HashMap<>());
            resultAssetSnapshotsForDate.putAll(latestAssetSnapshots);
            result.put(date, resultAssetSnapshotsForDate);
        }
        return result;
    }

    @Value.Check
    default void check() {
        LocalDate today = LocalDate.now();
        var assets = getAssets();
        var history = getHistoryByAsset();

        // Verify that all assets in the history are in the asset list
        for (String assetId : history.keySet()) {
            Preconditions.checkState(
                    assets.containsKey(assetId),
                    "Asset '%s' appears in the history but isn't defined in the assets list",
                    assetId
            );

            for (LocalDate date : getHistoryByAsset().get(assetId).keySet()) {
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
