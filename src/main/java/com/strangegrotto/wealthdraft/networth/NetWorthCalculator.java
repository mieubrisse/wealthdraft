package com.strangegrotto.wealthdraft.networth;

import com.strangegrotto.wealthdraft.errors.ValueOrGError;
import com.strangegrotto.wealthdraft.networth.assets.Asset;
import com.strangegrotto.wealthdraft.networth.projections.raw.RawAssetChange;
import com.strangegrotto.wealthdraft.networth.projections.ProjectionScenario;
import com.strangegrotto.wealthdraft.networth.projections.raw.RawProjections;
import com.strangegrotto.wealthdraft.validator.ValidationWarning;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class NetWorthCalculator {
    private static final int MAX_YEARS_TO_PROJECT = 80;

    // TODO Add support for "w" and "d"????
    private static final Pattern RELATIVE_DATE_PATTERN = Pattern.compile("^\\+([0-9]+)([ym])$");

    private static final int MONTHS_IN_YEAR = 12;

    private static final Logger log = LoggerFactory.getLogger(NetWorthCalculator.class);

    private final long staleAssetThresholdDays;
    private final int projectionDisplayIncrementYears;

    /**
     *
     * @param staleAssetThresholdDays Assets with a non-zero value that haven't received a new historical record in this many days will be flagged as stale
     * @param projectionDisplayYearIncrement When projecting into the future, new values for net worth will be calculated every N years
     */
    public NetWorthCalculator(long staleAssetThresholdDays, int projectionDisplayYearIncrement) {
        this.staleAssetThresholdDays = staleAssetThresholdDays;
        this.projectionDisplayIncrementYears = projectionDisplayYearIncrement;
    }

    public ValueOrGError<NetWorthCalculationResults> calculateNetWorth(Map<String, Asset> assets, RawProjections projections) {
        ImmutableNetWorthCalculationResults.Builder resultBuilder = ImmutableNetWorthCalculationResults.builder();

        ValueOrGError<List<ValidationWarning>> assetValidationResult = validateAssets(assets);
        if (assetValidationResult.hasError()) {
            log.error(assetValidationResult.getError().toString());
            return ValueOrGError.ofPropagatedErr(
                    assetValidationResult.getError(),
                    "An error occurred validating assets"
            );
        }
        resultBuilder.addAllWarnings(assetValidationResult.getValue());

        Map<LocalDate, Map<String, Long>> histNetWorthCheckpointValues = new HashMap<>();
        assets.forEach((assetId, asset) -> {
            asset.getHistorical().forEach((date, value) -> {
                Map<String, Long> assetValuesForDate = histNetWorthCheckpointValues.getOrDefault(date, new HashMap<>());
                assetValuesForDate.put(assetId, value);
                histNetWorthCheckpointValues.put(date, assetValuesForDate);
            });
        });

        List<LocalDate> histNetWorthCheckpointDates = histNetWorthCheckpointValues.keySet().stream()
                .sorted()
                .collect(Collectors.toList());

        Map<String, Long> latestHistAssetValues = new HashMap<>();
        for (LocalDate date : histNetWorthCheckpointDates) {
            Map<String, Long> assetValuesAtCheckpoint = histNetWorthCheckpointValues.get(date);
            latestHistAssetValues.putAll(assetValuesAtCheckpoint);
            long netWorthAtCheckpoint = latestHistAssetValues.values().stream()
                    .reduce(0L, (l, r) -> l + r);
            resultBuilder.putHistoricalNetWorth(date, netWorthAtCheckpoint);
        }

        for (Map.Entry<String, ProjectionScenario> projectionScenarioEntry : projections.getScenarios().entrySet()) {
            String projectionScenarioId = projectionScenarioEntry.getKey();
            ProjectionScenario projectionScenario = projectionScenarioEntry.getValue();

            Set<LocalDate> assetChangeDates = new HashSet<>();
            Map<LocalDate, Map<String, RawAssetChange>> assetChangesByDate = new HashMap<>();
            for (Map.Entry<String, Map<String, RawAssetChange>> assetEntry : projectionScenario.getChanges().entrySet()) {
                String assetId = assetEntry.getKey();
                for (Map.Entry<String, RawAssetChange> assetChangeEntry : assetEntry.getValue().entrySet()) {
                    String changeDateStr = assetChangeEntry.getKey();
                    RawAssetChange change = assetChangeEntry.getValue();

                    ValueOrGError<LocalDate> changeDateParseResult = parseChangeDateStr(changeDateStr);
                    if (changeDateParseResult.hasError()) {
                        return ValueOrGError.ofPropagatedErr(
                                changeDateParseResult.getError(),
                                "An error occurred parsing change date string '{}'",
                                changeDateStr
                        );
                    }
                    LocalDate changeDate = changeDateParseResult.getValue();

                    assetChangeDates.add(changeDate);
                    Map<String, RawAssetChange> assetChangesForDate = assetChangesByDate.getOrDefault(changeDate, new HashMap<>());
                    assetChangesForDate.put(assetId, change);
                    assetChangesByDate.put(changeDate, assetChangesForDate);
                }
            }

            Set<LocalDate> compoundingDates = new HashSet<>();
            LocalDate today = LocalDate.now();
            for (int i = 0; i < MONTHS_IN_YEAR * MAX_YEARS_TO_PROJECT; i++) {
                compoundingDates.add(today.plusMonths(i));
            }

            Set<LocalDate> allDatesOfInterest = new HashSet<>();
            allDatesOfInterest.addAll(assetChangeDates);
            allDatesOfInterest.addAll(compoundingDates);

            List<LocalDate> orderedDatesOfInterest = allDatesOfInterest.stream()
                    .sorted()
                    .collect(Collectors.toList());

            Map<String, Long> currentAssetValues = new HashMap<>(latestHistAssetValues);
            for (LocalDate date : orderedDatesOfInterest) {
                // First apply any asset value changes
                if (assetChangeDates.contains(date)) {
                    Map<String, RawAssetChange> assetChanges = assetChangesByDate.get(date);
                    assetChanges.forEach((assetId, change) -> {
                        long oldValue = currentAssetValues.get(assetId);
                        ValueOrGError<Long> applicationResult = change.apply(oldValue);
                        if (applicationResult.hasError()) {

                        }
                    });
                }
                if (compoundingDates.contains(date)) {

                }
            }

            // Add in the routine checkpoints
            List<LocalDate> projectionCheckpointDatesList = assetChangeDates.stream()
                    .sorted()
                    .collect(Collectors.toList());
            for (LocalDate checkpointDate : projectionCheckpointDatesList) {
                proje
            }
        }
        // TODO
        return null;
    }

    private ValueOrGError<List<ValidationWarning>> validateAssets(Map<String, Asset> assets) {
        if (assets.size() == 0) {
            return ValueOrGError.ofNewErr("At least one asset must be specified, but none were");
        }

        // Validate net worth
        List<ValidationWarning> warnings = new ArrayList<>();
        LocalDate staleThreshold = LocalDate.now().minusDays(this.staleAssetThresholdDays);
        log.debug("Stale historical asset value threshold: {}", staleThreshold);
        for (Map.Entry<String, Asset> assetEntry : assets.entrySet()) {
            String assetId = assetEntry.getKey();
            Asset asset = assetEntry.getValue();

            if (asset.getHistorical().size() == 0) {
                return ValueOrGError.ofNewErr(
                        "Asset with ID '{}' is specified but has no historical values",
                        assetId
                );
            }

            LocalDate latestDate = LocalDate.MIN;
            long latestValue = 0;
            for (Map.Entry<LocalDate, Long> historicalAssetValue : asset.getHistorical().entrySet()) {
                LocalDate date = historicalAssetValue.getKey();
                Long value = historicalAssetValue.getValue();
                if (date.isAfter(LocalDate.now())) {
                    return ValueOrGError.ofNewErr(
                            "Asset with ID '{}' has a historical value in the future, '{}'",
                            assetId,
                            date
                    );
                }

                if (date.isAfter(latestDate)) {
                    latestDate = date;
                    latestValue = value;
                }
            }

            log.debug("Latest historical value for asset '{}' is {} on {}", assetId, latestValue, latestDate);
            if (latestValue > 0 && latestDate.isBefore(staleThreshold)) {
                warnings.add(ValidationWarning.of(
                        "The latest historical record for asset with ID '{}' was on {}, which is greater than {} days ago",
                        assetId,
                        latestDate,
                        this.staleAssetThresholdDays
                ));
            }
        }
        return ValueOrGError.ofValue(warnings);
    }

    /**
     * Parses an asset change date string which may be of the form "yyyy-mm-dd", or relative like
     *  "+5y", "+5m", etc.
     * @param text Text to parse
     * @return The parsed text, or an error if the text couldn't be parsed
     */
    private static ValueOrGError<LocalDate> parseChangeDateStr(String text) {
        try {
            return ValueOrGError.ofValue(LocalDate.parse(text));
        } catch (DateTimeParseException e) {}

        Matcher matcher = RELATIVE_DATE_PATTERN.matcher(text);
        if (!matcher.find()) {
            return ValueOrGError.ofNewErr(
                    "Unable to parse relative date string '{}'",
                    text
            );
        }

        long numberOfUnits = Long.parseLong(matcher.group(0));
        String units = matcher.group(1);

        LocalDate result = LocalDate.now();
        switch (units) {
            case "y":
                result = result.plusYears(numberOfUnits);
                break;
            case "m":
                result = result.plusMonths(numberOfUnits);
                break;
            default:
                return ValueOrGError.ofNewErr(
                        "Unrecognized relative date unit '{}'; this likely indicates a code error",
                        units
                );
        }
        return ValueOrGError.ofValue(result);
    }
}
