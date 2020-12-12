package com.strangegrotto.wealthdraft.networth.projections;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import com.strangegrotto.wealthdraft.networth.Asset;
import com.strangegrotto.wealthdraft.networth.AssetChange;
import com.strangegrotto.wealthdraft.networth.AssetSnapshot;
import com.strangegrotto.wealthdraft.networth.AssetType;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class ProjectionScenario {
    private final String name;
    private final Map<String, Asset> assets;
    private final Map<String, ListMultimap<LocalDate, AssetChange<?>>> assetChanges;

    public ProjectionScenario(String name, Map<String, Asset> assets, Map<String, ListMultimap<LocalDate, AssetChange<?>>> assetChanges) {
        this.name = name;
        this.assets = assets;
        this.assetChanges = assetChanges;
    }

    public <SNAPSHOT extends AssetSnapshot> ListMultimap<LocalDate, AssetChange<SNAPSHOT>> getChangesForAsset(String assetId, Class<SNAPSHOT> snapshotType) throws ClassCastException {
        Asset asset = this.assets.get(assetId);
        AssetType assetType = asset.getType();
        Class<? extends AssetSnapshot> actualSnapshotType = assetType.getSnapshotType();
        if (!snapshotType.equals(actualSnapshotType)) {
            throw new ClassCastException("Expected asset '" + assetId
                    + "' snapshot type to be '" + assetType + "' but is " +
                    "actually of type '" + actualSnapshotType + "'");
        }

        ListMultimap<LocalDate, AssetChange<SNAPSHOT>> castedChangesForAsset = MultimapBuilder.treeKeys().arrayListValues().build();
        ListMultimap<LocalDate, AssetChange<?>> uncastedChangesForAsset = this.assetChanges.get(assetId);
        for (LocalDate date : uncastedChangesForAsset.keySet()) {
            List<AssetChange<?>> uncastedAssetChangesOnDate = uncastedChangesForAsset.get(date);
            for (AssetChange<?> uncastedAssetChange : uncastedAssetChangesOnDate) {
                AssetChange<SNAPSHOT> castedAssetChange = (AssetChange<SNAPSHOT>)uncastedAssetChange;
                castedChangesForAsset.put(date, castedAssetChange);
            }
        }
        return castedChangesForAsset;
    }
}
