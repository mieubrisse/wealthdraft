package com.strangegrotto.wealthdraft.networth.history;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.strangegrotto.wealthdraft.assets.definition.Asset;
import com.strangegrotto.wealthdraft.assets.temporal.AssetSnapshot;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class AssetsHistoryDeserializer extends JsonDeserializer<AssetsHistory> {
    private final Map<String, Asset<?, ?>> assets;

    public AssetsHistoryDeserializer(Map<String, Asset<?, ?>> assets) {
        this.assets = assets;
    }

    @Override
    public AssetsHistory deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        // We need to use ObjectMapper.convertValue, but ObjectCodec doesn't have it on it
        var mapper = (ObjectMapper) parser.getCodec();
        Map<String, Map<LocalDate, Map<String, String>>> raw = parser.readValueAs(
                new TypeReference<Map<String, Map<LocalDate, Map<String, String>>>>(){}
        );

        LocalDate today = LocalDate.now();

        var parsedAssetSnapshots = new HashMap<String, SortedMap<LocalDate, AssetSnapshot>>();
        for (var assetId : raw.keySet()) {
            var unparsedSnapshotsForAsset = raw.get(assetId);
            Preconditions.checkState(
                    this.assets.containsKey(assetId),
                    "Asset ID '%s' doesn't match any known asset"
            );
            var asset = this.assets.get(assetId);
            var snapshotType = asset.getSnapshotType();

            var parsedSnapshotsForAsset = new TreeMap<LocalDate, AssetSnapshot>();
            for (var date : unparsedSnapshotsForAsset.keySet()) {
                var unparsedSnapshot = unparsedSnapshotsForAsset.get(date);
                var parsedSnapshot = mapper.convertValue(unparsedSnapshot, snapshotType);
                parsedSnapshotsForAsset.put(date, parsedSnapshot);
            }
            parsedAssetSnapshots.put(assetId, parsedSnapshotsForAsset);
        }

        return ImmAssetsHistory.of(this.assets, parsedAssetSnapshots);
    };
}
