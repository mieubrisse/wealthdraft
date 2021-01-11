package com.strangegrotto.wealthdraft.assethistory.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import com.google.common.base.Preconditions;
import com.strangegrotto.wealthdraft.assets.api.AssetsStore;
import com.strangegrotto.wealthdraft.assets.api.types.Asset;
import com.strangegrotto.wealthdraft.assets.impl.SerAsset;
import com.strangegrotto.wealthdraft.assethistory.api.types.AssetSnapshot;
import com.strangegrotto.wealthdraft.networth.history.ImmSerAssetsHistory;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class SerAssetsHistoryDeserializer extends JsonDeserializer<SerAssetsHistory> {
    private final AssetsStore assetsStore;

    public SerAssetsHistoryDeserializer(AssetsStore assetsStore) {
        this.assetsStore = assetsStore;
    }

    @Override
    public SerAssetsHistory deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
        // We need to use ObjectMapper.convertValue, but ObjectCodec doesn't have it on it
        var mapper = (ObjectMapper) parser.getCodec();
        Map<LocalDate, Map<String, Map<String, String>>> raw = parser.readValueAs(
                new TypeReference<Map<LocalDate, Map<String, Map<String, String>>>>(){}
        );

        var assets = this.assetsStore.getAssets();

        var parsedAssetSnapshots = new TreeMap<LocalDate, Map<String, AssetSnapshot<?>>>();
        for (var rawEntry : raw.entrySet()) {
            var date = rawEntry.getKey();
            var unparsedAssetSnapshots = rawEntry.getValue();


            var parsedSnapshotsForAsset = new HashMap<String, AssetSnapshot<?>>();
            for (var unparsedAssetSnapshotEntry : unparsedAssetSnapshots.entrySet()) {
                var assetId = unparsedAssetSnapshotEntry.getKey();
                var unparsedSnapshot = unparsedAssetSnapshotEntry.getValue();
                Preconditions.checkState(
                        assets.containsKey(assetId),
                        "Asset ID '%s' doesn't match any known asset"
                );

                var asset = assets.get(assetId);
                var assetType = asset.getType();
                var parsedSnapshot = mapper.convertValue(unparsedSnapshot, assetType.getSnapshotClass());
                parsedSnapshotsForAsset.put(assetId, parsedSnapshot);
            }
            parsedAssetSnapshots.put(date, parsedSnapshotsForAsset);
        }

        return ImmSerAssetsHistory.of(parsedAssetSnapshots);
    }
}
