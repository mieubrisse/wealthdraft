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
import java.util.Set;

public class AssetsWithHistoryDeserializer extends JsonDeserializer<AssetsWithHistory> {
    @WealthdraftImmutableStyle
    @Value.Immutable
    @JsonDeserialize(as = ImmRawAssetsWithHistory.class)
    interface RawAssetsWithHistory {
        Map<String, CustomTagDefinition> getCustomTags();

        @Value.Parameter
        Map<String, Asset> getAssets();

        @Value.Parameter
        Map<String, Map<LocalDate, Map<String, String>>> getHistory();
    }

    @Override
    public AssetsWithHistory deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        // We need to use ObjectMapper.convertValue, but ObjectCodec doesn't have it on it
        ObjectMapper mapper = (ObjectMapper) parser.getCodec();
        RawAssetsWithHistory raw = parser.readValueAs(RawAssetsWithHistory.class);
        var assets = raw.getAssets();

        // Validate that the custom tag definitions are valid
        var customTagDefinitions = raw.getCustomTags();
        for (String customTagName : customTagDefinitions.keySet()) {
            var definition = customTagDefinitions.get(customTagName);
            var allowedValues = definition.getAllowedValues();
            var defaultValueOpt = definition.getDefaultValue();
            if (allowedValues.size() > 0 && defaultValueOpt.isPresent()) {
                String defaultValue = definition.getDefaultValue().get();
                if (!allowedValues.contains(defaultValue)) {
                    throw new JsonParseException(parser, "Custom tag '" + customTagName + "' has default value '" +
                            defaultValue + "' defined, but that value isn't in the allowed values list");
                }
            }
        }

        // Build default tags

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
                var tagValue = customTagsForAsset.get(tagName);
                if
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
            var assetType = assets.get(assetId).getType();
            var snapshotType = assetType.getSnapshotType();

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
