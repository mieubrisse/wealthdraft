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
        Map<String, CustomTagDefinition> getCustomTags();

        @Value.Parameter
        Map<String, AbstractAsset> getAssets();

        @Value.Parameter
        Map<String, Map<LocalDate, Map<String, String>>> getHistory();
    }

    @Override
    public AssetsWithHistory deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        // We need to use ObjectMapper.convertValue, but ObjectCodec doesn't have it on it
        ObjectMapper mapper = (ObjectMapper) parser.getCodec();
        RawAssetsWithHistory raw = parser.readValueAs(RawAssetsWithHistory.class);
        var assets = raw.getAssets();
        var customTagDefinitions = raw.getCustomTags();

        // Validate that all the custom tags being used by the assets are predeclared, and have the values
        //  expected
        for (var assetId : assets.keySet()) {
            var asset = assets.get(assetId);
            var customTagsForAsset = asset.getCustomTags();
            for (String tagName : customTagsForAsset.keySet()) {
                if (!customTagDefinitions.containsKey(tagName)) {
                    throw new JsonParseException(
                            parser,
                            "Asset '" + assetId + "' uses tag '" + tagName + "', but that tag isn't " +
                                    "declared in the custom tag registration"
                    );
                }
                var customTagDefinition = customTagDefinitions.get(tagName);
                var allowedTagValues = customTagDefinition.getAllowedValues();
                var tagValue = customTagsForAsset.get(tagName);
                if (allowedTagValues.size() > 0 && !allowedTagValues.contains(tagValue)) {
                    throw new JsonParseException(
                            parser,
                            "Asset '" + assetId + "' has value '" + tagValue + "' for custom tag '" + tagName + ", which" +
                                    " isn't in the set of allowed values"
                    );
                }
            }
        }

        LocalDate today = LocalDate.now();

        var unparsedAssetSnapshots = raw.getHistory();
        var parsedAssetSnapshots = new HashMap<String, Map<LocalDate, AssetSnapshot>>();
        for (String assetId : unparsedAssetSnapshots.keySet()) {
            var unparsedSnapshotsForAsset = unparsedAssetSnapshots.get(assetId);

            if (!assets.containsKey(assetId)) {
                throw new JsonParseException(parser, "Asset ID '" + assetId + "' doesn't correspond to any known asset");
            }
            var asset = assets.get(assetId);
            var snapshotType = asset.getSnapshotType();

            var parsedSnapshotsForAsset = new HashMap<LocalDate, AssetSnapshot>();
            for (LocalDate date : unparsedSnapshotsForAsset.keySet()) {
                if (date.isAfter(today)) {
                    throw new JsonParseException(
                            parser,
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
