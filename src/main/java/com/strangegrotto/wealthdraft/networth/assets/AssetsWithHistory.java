package com.strangegrotto.wealthdraft.networth.assets;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import org.immutables.value.Value;

import java.time.LocalDate;
import java.util.Map;

@WealthdraftImmutableStyle
@Value.Immutable
@JsonDeserialize(using = AssetsWithHistoryDeserializer.class)
public interface AssetsWithHistory {
    @Value.Parameter
    Map<String, Asset> getAssets();

    @Value.Parameter
    Map<String, Map<LocalDate, AssetSnapshot>> getHistory();

    @Value.Check
    default void check() {
        LocalDate today = LocalDate.now();

        // Verify that all assets in history are in the asset list
        for (String assetId : getHistory().keySet()) {
            Preconditions.checkState(
                    getAssets().containsKey(assetId),
                    "Asset ID %s appears in the history but isn't defined in the assets section",
                    assetId);

            for (LocalDate date : getHistory().get(assetId).keySet()) {
                Preconditions.checkState(
                        !date.isAfter(today),
                        "Asset %s has a historical record with date %s, which is in the future",
                        assetId,
                        date);
            }
        }
    }
}
