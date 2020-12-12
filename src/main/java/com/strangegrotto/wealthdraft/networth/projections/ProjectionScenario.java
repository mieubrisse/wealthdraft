package com.strangegrotto.wealthdraft.networth.projections;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.time.LocalDate;
import java.util.Map;
import java.util.SortedMap;

@Value.Immutable
@JsonDeserialize(as = ImmutableProjectionScenario.class)
public interface ProjectionScenario {
    String getName();

    String getBase();

    // Map<String, Asset> getAssets();

    SortedMap<LocalDate, SortedMap<String, AssetChange<?>>> getAssetChanges();

    /*
    // TODO Remove?? Do we even need this????
    @Value.Derived
    public <SNAPSHOT extends AssetSnapshot> ListMultimap<LocalDate, AssetChange<SNAPSHOT>> getChangesForAsset(String assetId, Class<SNAPSHOT> snapshotType) throws ClassCastException {
        // TODO Remove so we don't have to have a tie to Assets
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

     */
}
