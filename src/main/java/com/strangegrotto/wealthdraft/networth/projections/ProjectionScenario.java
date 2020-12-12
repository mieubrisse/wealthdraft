package com.strangegrotto.wealthdraft.networth.projections;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import com.strangegrotto.wealthdraft.networth.Asset;
import com.strangegrotto.wealthdraft.networth.AssetSnapshot;
import com.strangegrotto.wealthdraft.networth.AssetType;
import org.immutables.value.Value;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Value.Immutable
public abstract class ProjectionScenario {
    public abstract String getName();

    public abstract String getBase();

    protected abstract Map<String, Asset> getAssets();

    // Package-private, since this is only used during deserialization
    protected abstract Map<String, ListMultimap<LocalDate, AssetChange<?>>> getAssetChanges();

    @Value.Derived
    public <SNAPSHOT extends AssetSnapshot> ListMultimap<LocalDate, AssetChange<SNAPSHOT>> getChangesForAsset(String assetId, Class<SNAPSHOT> snapshotType) throws ClassCastException {
        Asset asset = getAssets().get(assetId);
        AssetType assetType = asset.getType();
        Class<? extends AssetSnapshot> actualSnapshotType = assetType.getSnapshotType();
        if (!snapshotType.equals(actualSnapshotType)) {
            throw new ClassCastException("Expected asset '" + assetId
                    + "' snapshot type to be '" + assetType + "' but is " +
                    "actually of type '" + actualSnapshotType + "'");
        }

        ListMultimap<LocalDate, AssetChange<SNAPSHOT>> castedChangesForAsset = MultimapBuilder.treeKeys().arrayListValues().build();
        ListMultimap<LocalDate, AssetChange<?>> uncastedChangesForAsset = getAssetChanges().get(assetId);
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
