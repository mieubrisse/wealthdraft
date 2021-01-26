package com.strangegrotto.wealthdraft.backend.projections.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;
import com.strangegrotto.wealthdraft.backend.assets.api.AssetsStore;
import com.strangegrotto.wealthdraft.backend.assets.api.types.Asset;
import com.strangegrotto.wealthdraft.backend.projections.api.types.AssetChange;
import com.strangegrotto.wealthdraft.errors.Gerr;
import com.strangegrotto.wealthdraft.errors.ValOrGerr;

import javax.annotation.Nullable;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class SerProjectionsDeserializer extends JsonDeserializer<SerProjections> {
    // TODO Add support for "w" and "d"????
    private static final Pattern RELATIVE_DATE_PATTERN = Pattern.compile("^\\+([0-9]+)([ym])$");

    // TODO swap this to immutables-generated interface
    private static class RawProjectionScenario {
        @JsonProperty
        public String name;

        // This property is optional, but we can't use Optional because Jackson will set it to null regardless
        @JsonProperty
        public @Nullable String base;

        // relativeDateStr -> (assetId -> (key-vals representing change))
        @JsonProperty
        public Map<String, Map<String, Map<String, String>>> changes;
    }

    // TODO swap this to immutables-generated interface
    private static class RawjProjections {
        @JsonProperty
        public BigDecimal defaultAnnualGrowth;

        @JsonProperty
        public Map<String, RawProjectionScenario> scenarios;
    }

    private final AssetsStore assetsStore;

    public SerProjectionsDeserializer(AssetsStore assetsStore) {
        this.assetsStore = assetsStore;
    }

    @Override
    public SerProjections deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        RawjProjections rawjProjections = p.readValueAs(RawjProjections.class);

        // It's VERY unclear to me how to call the needed convertValue function without doing
        //  this hacky cast
        ObjectMapper mapper = (ObjectMapper) p.getCodec();

        var rawScenarios = rawjProjections.scenarios;
        Map<String, SerProjectionScenario> notUnrolledParsedScenarios = new HashMap<>();
        for (var rawScenarioEntry : rawScenarios.entrySet()) {
            var scenarioId = rawScenarioEntry.getKey();
            var rawScenario = rawScenarioEntry.getValue();
            var parsedScenarioOrErr = parseProjectionScenario(
                    scenarioId,
                    rawScenario,
                    this.assetsStore.getAssets(),
                    mapper);
            if (parsedScenarioOrErr.hasGerr()) {
                throw new IllegalStateException(parsedScenarioOrErr.getGerr().toString());
            }
            notUnrolledParsedScenarios.put(scenarioId, parsedScenarioOrErr.getVal());
        }

        return ImmSerProjections.of(rawjProjections.defaultAnnualGrowth, notUnrolledParsedScenarios);
    }

    // TODO Move the not-strictly-necessary validation to the validation func in SimpleProjectionsStoreFactory
    /**
     * Parses & validates the JSON for a projection scenario into the type of object that we need
     */
    private static ValOrGerr<SerProjectionScenario> parseProjectionScenario(
            String scenarioId,
            RawProjectionScenario rawScenario,
            Map<String, Asset> assets,
            ObjectMapper mapper) {
        var parsedAssetChanges = new TreeMap<LocalDate, Map<String, AssetChange>>();
        for (String relativeDateStr : rawScenario.changes.keySet()) {
            Map<String, Map<String, String>> unparsedAssetChangesOnDate = rawScenario.changes.get(relativeDateStr);

            var actualDateOrErr = parseRelativeDateStr(relativeDateStr);
            if (actualDateOrErr.hasGerr()) {
                return ValOrGerr.newGerr(
                        "An error occurred parsing relative date string '{}' for scenario {}",
                        relativeDateStr,
                        scenarioId
                );
            }
            LocalDate actualDate = actualDateOrErr.getVal();

            if (actualDate.isBefore(LocalDate.now())) {
                return ValOrGerr.newGerr(
                        "Projection scenario {} cannot be used because it has asset changes in the past, on date {}",
                        scenarioId,
                        actualDate
                );
            }

            for (var assetChangeEntry : unparsedAssetChangesOnDate.entrySet()) {
                var assetId = assetChangeEntry.getKey();
                var unparsedAssetChange = assetChangeEntry.getValue();

                if (!assets.containsKey(assetId)) {
                    return ValOrGerr.newGerr(
                            "Projection {} defines a change for asset {} but this asset doesn't exist",
                            scenarioId,
                            assetId
                    );
                }

                var referencedAsset = assets.get(assetId);
                var referencedAssetType = referencedAsset.getType();
                AssetChange parsedAssetChange;
                try {
                    parsedAssetChange = mapper.convertValue(unparsedAssetChange, referencedAssetType.getChangeClass());
                } catch (IllegalArgumentException e) {
                    return ValOrGerr.propGerr(
                            Gerr.newGerr(e.getMessage()),
                            "An error occurred parsing the asset change"
                    );
                }

                Map<String, AssetChange> assetChangesForDate = parsedAssetChanges.getOrDefault(
                        actualDate,
                        new HashMap<>()
                );
                // TODO support changing an asset multiple times in a given day
                if (assetChangesForDate.containsKey(assetId)) {
                    return ValOrGerr.newGerr(
                            "Scenario {} has more than one change for asset {} on date {}",
                            scenarioId,
                            assetId,
                            actualDate
                    );
                }
                assetChangesForDate.put(assetId, parsedAssetChange);
                parsedAssetChanges.put(actualDate, assetChangesForDate);
            }
        }

        return ValOrGerr.val(ImmSerProjectionScenario.of(
                rawScenario.name,
                Optional.ofNullable(rawScenario.base),
                parsedAssetChanges
        ));
    }

    @VisibleForTesting
    static ValOrGerr<LocalDate> parseRelativeDateStr(String rawStr) {
        try {
            LocalDate asLocalDate = LocalDate.parse(rawStr);
            return ValOrGerr.val(asLocalDate);
        } catch (DateTimeParseException ignored) {}

        Matcher matcher = RELATIVE_DATE_PATTERN.matcher(rawStr);
        if (!matcher.find()) {
            return ValOrGerr.newGerr(
                    "Relative date str '{}' does not match expected pattern {}",
                    rawStr,
                    RELATIVE_DATE_PATTERN.toString()
            );
        }

        // Group 0 is the whole string, which is why we start with index 1
        long numberOfUnits = Long.parseLong(matcher.group(1));
        String units = matcher.group(2);

        LocalDate result = LocalDate.now();
        switch (units) {
            case "y":
                result = result.plusYears(numberOfUnits);
                break;
            case "m":
                result = result.plusMonths(numberOfUnits);
                break;
            default:
                return ValOrGerr.newGerr("Unrecognized unit number '" + units + "'");
        }
        return ValOrGerr.val(result);
    }
}
