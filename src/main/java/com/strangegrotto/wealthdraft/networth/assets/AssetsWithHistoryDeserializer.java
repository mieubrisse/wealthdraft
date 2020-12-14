package com.strangegrotto.wealthdraft.networth.assets;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import org.immutables.value.Value;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class AssetsWithHistoryDeserializer extends JsonDeserializer<AssetsWithHistory> {
    @WealthdraftImmutableStyle
    @Value.Immutable
    @JsonDeserialize(as = ImmRawAssetsWithHistory.class)
    interface RawAssetsWithHistory {
        @Value.Parameter
        Map<String, Asset> getAssets();

        @Value.Parameter
        Map<String, Map<LocalDate, Map<String, String>>> getHistory();
    }

    @Override
    public AssetsWithHistory deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        // We need to use ObjectMapper.convertValue, but ObjectCodec doesn't have it on it
        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        RawAssetsWithHistory raw = p.readValueAs(RawAssetsWithHistory.class);
        Map<String, Asset> assets = raw.getAssets();

        LocalDate today = LocalDate.now();

        var unparsedAssetSnapshots = raw.getHistory();
        var parsedAssetSnapshots = new HashMap<String, Map<LocalDate, AssetSnapshot>>();
        for (String assetId : unparsedAssetSnapshots.keySet()) {
            var unparsedSnapshotsForAsset = unparsedAssetSnapshots.get(assetId);

            if (!assets.containsKey(assetId)) {
                throw new JsonParseException(p, "Asset ID '" + assetId + "' doesn't correspond to any known asset");
            }
            var assetType = assets.get(assetId).getType();
            var snapshotType = assetType.getSnapshotType();

            var parsedSnapshotsForAsset = new HashMap<LocalDate, AssetSnapshot>();
            for (LocalDate date : unparsedSnapshotsForAsset.keySet()) {
                if (date.isAfter(today)) {
                    throw new JsonParseException(
                            p,
                            "Asset ID '" + assetId + "' has record dated '" + date + "', which is in the future"
                    );
                }
                var unparsedSnapshot = unparsedSnapshotsForAsset.get(date);
                var parsedSnapshot = mapper.convertValue(unparsedSnapshot, snapshotType);
                parsedSnapshotsForAsset.put(date, parsedSnapshot);
            }
            parsedAssetSnapshots.put(assetId, parsedSnapshotsForAsset);
        }

        return ImmAssetsWithHistory.of(assets, parsedAssetSnapshots);
    };
}
