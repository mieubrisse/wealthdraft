package com.strangegrotto.wealthdraft.networth.projections;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.strangegrotto.wealthdraft.assets.impl.SerAsset;
import com.strangegrotto.wealthdraft.errors.Gerr;
import com.strangegrotto.wealthdraft.errors.ValOrGerr;
import com.strangegrotto.wealthdraft.projections.impl.temporal.AssetChange;

import javax.annotation.Nullable;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProjectionsDeserializer extends JsonDeserializer<Projections> {
    // TODO Add support for "w" and "d"????
    private static final Pattern RELATIVE_DATE_PATTERN = Pattern.compile("^\\+([0-9]+)([ym])$");

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

    private static class RawjProjections {
        @JsonProperty
        public BigDecimal defaultAnnualGrowth;

        @JsonProperty
        public Map<String, RawProjectionScenario> scenarios;
    }

    private static class NotUnrolledParsedScenario {
        public final String name;
        @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
        public final Optional<String> base;
        public final Map<String, SerAsset> assets;
        public final Map<LocalDate, Map<String, AssetChange>> assetChanges;

        private NotUnrolledParsedScenario(
                String name,
                @SuppressWarnings("OptionalUsedAsFieldOrParameterType") Optional<String> base,
                Map<String, SerAsset> assets,
                Map<LocalDate, Map<String, AssetChange>> assetChanges) {
            this.name = name;
            this.base = base;
            this.assets = assets;
            this.assetChanges = assetChanges;
        }
    }

    private final Map<String, SerAsset> assets;

    public ProjectionsDeserializer(Map<String, SerAsset> assets) {
        this.assets = assets;
    }

    @Override
    public Projections deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        RawjProjections rawjProjections = p.readValueAs(RawjProjections.class);

        // It's VERY unclear to me how to call the needed convertValue function without doing
        //  this hacky cast
        ObjectMapper mapper = (ObjectMapper) p.getCodec();

        Map<String, RawProjectionScenario> rawScenarios = rawjProjections.scenarios;
        Map<String, ValOrGerr<NotUnrolledParsedScenario>> notUnrolledParsedScenarios = new HashMap<>();
        rawScenarios.forEach((scenarioId, rawScenario) -> {
            ValOrGerr<NotUnrolledParsedScenario> parsedScenarioOrErr = parseProjectionScenario(scenarioId, rawScenario, this.assets, mapper);
            notUnrolledParsedScenarios.put(scenarioId, parsedScenarioOrErr);
        });

        Map<String, ValOrGerr<ProjectionScenario>> unrolledParsedScenarios = new HashMap<>();
        for (String scenarioId : notUnrolledParsedScenarios.keySet()) {
            var unrolledScenarioOrErr = unrollScenarioAssetChanges(scenarioId, notUnrolledParsedScenarios);
            unrolledParsedScenarios.put(scenarioId, unrolledScenarioOrErr);
        }

        return ImmProjections.of(rawjProjections.defaultAnnualGrowth, unrolledParsedScenarios);
    }

    /**
     * Parses & validates the JSON for a projection scenario into the type of object that we need
     */
    private static ValOrGerr<NotUnrolledParsedScenario> parseProjectionScenario(
            String scenarioId,
            RawProjectionScenario rawScenario,
            Map<String, SerAsset> assets,
            ObjectMapper mapper) {
        Map<LocalDate, Map<String, AssetChange>> parsedAssetChanges = new HashMap<>();
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

        return ValOrGerr.val(new NotUnrolledParsedScenario(
                rawScenario.name,
                Optional.ofNullable(rawScenario.base),
                assets,
                parsedAssetChanges
        ));
    }

    /**
     * For scenarios that are based on other scenarios, recursively unrolls the asset changes all the way
     *  through the dependency tree.
     */
    private static ValOrGerr<ProjectionScenario> unrollScenarioAssetChanges(
            String scenarioId,
            Map<String, ValOrGerr<NotUnrolledParsedScenario>> notUnrolledScenarios) {
        // We can't unroll a scenario if an error occurred trying to parse it
        ValOrGerr<NotUnrolledParsedScenario> notUnrolledScenarioOrErr = notUnrolledScenarios.get(scenarioId);
        if (notUnrolledScenarioOrErr.hasGerr()) {
            return ValOrGerr.propGerr(
                    notUnrolledScenarioOrErr.getGerr(),
                    "Couldn't unroll dependencies for scenario '{}' because an error occurred during parsing",
                    scenarioId
            );
        }

        ValOrGerr<Stack<String>> scenarioIdsToVisitOrErr = getScenarioIdsToVisit(scenarioId, notUnrolledScenarios);
        if (scenarioIdsToVisitOrErr.hasGerr()) {
            return ValOrGerr.propGerr(
                    scenarioIdsToVisitOrErr.getGerr(),
                    "An error occurred getting the list of dependency scenarios for scenario '{}'",
                    scenarioId
            );
        }
        Stack<String> scenarioIdsToVisit = scenarioIdsToVisitOrErr.getVal();

        // Now that we have the full list of dependency scenarios to visit, in order, loop through them and build a list
        //  asset changes that we'll return
        var unrolledAssetChanges = new TreeMap<LocalDate, Map<String, AssetChange>>();
        for (String scenarioIdToVisit : scenarioIdsToVisit) {
            ValOrGerr<NotUnrolledParsedScenario> scenarioToVisitOrErr = notUnrolledScenarios.get(scenarioIdToVisit);
            Preconditions.checkState(
                    !scenarioToVisitOrErr.hasGerr(),
                    "Our list of scenarios to visit somehow contains an errored scenario; this is a code bug, " +
                            "since we shouldn't have even received the list of scenarios to visit if any of the " +
                            "dependency scenarios had parse errors");
            NotUnrolledParsedScenario scenarioToVisit = scenarioToVisitOrErr.getVal();

            Map<LocalDate, Map<String, AssetChange>> scenarioAssetChanges = scenarioToVisit.assetChanges;
            for (var changesForDateEntry : scenarioAssetChanges.entrySet()) {
                var date = changesForDateEntry.getKey();
                var scenarioChangesForDate = changesForDateEntry.getValue();

                Map<String, AssetChange> unrolledChangesForDate = unrolledAssetChanges.getOrDefault(
                        date,
                        new HashMap<>()
                );
                for (var assetChangeEntry : scenarioChangesForDate.entrySet()) {
                    var assetId = assetChangeEntry.getKey();
                    var assetChange = assetChangeEntry.getValue();

                    if (unrolledChangesForDate.containsKey(assetId)) {
                        return ValOrGerr.newGerr(
                                "Scenario {} depends on scenario {}, which results in a duplicate change for {} on date {}",
                                scenarioId,
                                scenarioIdToVisit,
                                assetId,
                                date
                        );
                    }
                    unrolledChangesForDate.put(assetId, assetChange);
                }
                unrolledAssetChanges.put(date, unrolledChangesForDate);
            }
        }

        NotUnrolledParsedScenario notUnrolledScenario = notUnrolledScenarioOrErr.getVal();
        ProjectionScenario result = ImmProjectionScenario.of(
                notUnrolledScenario.name,
                unrolledAssetChanges
        ).withBase(notUnrolledScenario.base);
        return ValOrGerr.val(result);
    }

    private static ValOrGerr<Stack<String>> getScenarioIdsToVisit(String scenarioId, Map<String, ValOrGerr<NotUnrolledParsedScenario>> notUnrolledScenarios) {
        Optional<String> baseIdOpt = Optional.of(scenarioId);
        Stack<String> scenarioIdsToVisit = new Stack<>();
        Set<String> visitedScenarioIds = new HashSet<>();
        while (baseIdOpt.isPresent()) {
            String baseId = baseIdOpt.get();
            if (visitedScenarioIds.contains(baseId)) {
                return ValOrGerr.newGerr(
                        "Could not get list of dependency scenarios for scenario {} due to a dependency cycle; dependency {} is visited twice",
                        scenarioId,
                        baseId
                );
            }

            if (!notUnrolledScenarios.containsKey(baseId)) {
                return ValOrGerr.newGerr(
                        "Could not get list of dependency scenarios for scenario {} due to a dependency on a nonexistent scenario ID {}",
                        scenarioId,
                        baseId
                );
            }

            ValOrGerr<NotUnrolledParsedScenario> notUnrolledBaseScenarioOrErr = notUnrolledScenarios.get(baseId);
            if (notUnrolledBaseScenarioOrErr.hasGerr()) {
                return ValOrGerr.newGerr(
                        "Could not get list of dependency scenarios for scenario '{}'; it depends on scenario '{}' which has a parsing error",
                        scenarioId,
                        baseId
                );
            }
            NotUnrolledParsedScenario notUnrolledBaseScenario = notUnrolledBaseScenarioOrErr.getVal();

            scenarioIdsToVisit.push(baseId);
            visitedScenarioIds.add(baseId);
            baseIdOpt = notUnrolledBaseScenario.base;
        }
        return ValOrGerr.val(scenarioIdsToVisit);
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
