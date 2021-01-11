package com.strangegrotto.wealthdraft.networth.history;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.strangegrotto.wealthdraft.assets.impl.SerAsset;
import com.strangegrotto.wealthdraft.assets.temporal.AssetSnapshot;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class AssetsHistoryDeserializer extends JsonDeserializer<AssetsHistory> {
    private final Map<String, SerAsset> assets;

    public AssetsHistoryDeserializer(Map<String, SerAsset> assets) {
        this.assets = assets;
    }

    @Override
    public AssetsHistory deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
        // We need to use ObjectMapper.convertValue, but ObjectCodec doesn't have it on it
        var mapper = (ObjectMapper) parser.getCodec();
        Map<LocalDate, Map<String, Map<String, String>>> raw = parser.readValueAs(
                new TypeReference<Map<LocalDate, Map<String, Map<String, String>>>>(){}
        );

        var parsedAssetSnapshots = new TreeMap<LocalDate, Map<String, AssetSnapshot<?>>>();
        for (var rawEntry : raw.entrySet()) {
            var date = rawEntry.getKey();
            var unparsedAssetSnapshots = rawEntry.getValue();


            var parsedSnapshotsForAsset = new HashMap<String, AssetSnapshot<?>>();
            for (var unparsedAssetSnapshotEntry : unparsedAssetSnapshots.entrySet()) {
                var assetId = unparsedAssetSnapshotEntry.getKey();
                var unparsedSnapshot = unparsedAssetSnapshotEntry.getValue();
                Preconditions.checkState(
                        this.assets.containsKey(assetId),
                        "Asset ID '%s' doesn't match any known asset"
                );

                var asset = this.assets.get(assetId);
                var assetType = asset.getType();
                var parsedSnapshot = mapper.convertValue(unparsedSnapshot, assetType.getSnapshotClass());
                parsedSnapshotsForAsset.put(assetId, parsedSnapshot);
            }
            parsedAssetSnapshots.put(date, parsedSnapshotsForAsset);
        }

        return ImmAssetsHistory.of(this.assets, parsedAssetSnapshots);
    }
}
