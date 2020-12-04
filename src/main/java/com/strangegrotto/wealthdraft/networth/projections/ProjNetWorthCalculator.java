package com.strangegrotto.wealthdraft.networth.projections;

import com.strangegrotto.wealthdraft.errors.ValOrGerr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ProjNetWorthCalculator {
    private static final int MAX_YEARS_TO_PROJECT = 80;

    // TODO Add support for "w" and "d"????
    private static final Pattern RELATIVE_DATE_PATTERN = Pattern.compile("^\\+([0-9]+)([ym])$");

    private static final int MONTHS_IN_YEAR = 12;

    private static final Logger log = LoggerFactory.getLogger(ProjNetWorthCalculator.class);

    private final int projectionDisplayIncrementYears;

    /**
     *
     * @param projectionDisplayYearIncrement When projecting into the future, new values for net worth will be calculated every N years
     */
    public ProjNetWorthCalculator(int projectionDisplayYearIncrement) {
        this.projectionDisplayIncrementYears = projectionDisplayYearIncrement;
    }

    // TODO write tests for this!!
    public ProjNetWorthCalcResults calculateNetWorthProjections(Map<String, Long> latestHistAssetValues, Projections projections) {
        ImmutableProjNetWorthCalcResults.Builder resultBuilder = ImmutableProjNetWorthCalcResults.builder();

        // Convert YoY growth into MoM
        double defaultMonthlyMultiplier = Math.pow(1 + projections.getDefaultAnnualGrowth(), 1D / 12D);
        log.debug("Default monthly growth: {}", defaultMonthlyMultiplier);

        for (Map.Entry<String, ProjectionScenario> projectionScenarioEntry : projections.getScenarios().entrySet()) {
            String scenarioId = projectionScenarioEntry.getKey();
            ProjectionScenario projectionScenario = projectionScenarioEntry.getValue();

            ValOrGerr<SortedMap<LocalDate, Long>> netWorthProjectionsOrErr = calcScenarioNetWorthProjections(
                    defaultMonthlyMultiplier,
                    projectionScenario,
                    latestHistAssetValues,
                    this.projectionDisplayIncrementYears
            );
            if (netWorthProjectionsOrErr.hasGerr()) {
                resultBuilder.putProjNetWorths(
                        scenarioId,
                        ValOrGerr.propGerr(
                                netWorthProjectionsOrErr.getGerr(),
                                "An error occurred calculating the net worth projections for scenario with ID '{}'",
                                scenarioId
                        )
                );
            } else {
                SortedMap<LocalDate, Long> projectedNetWorths = netWorthProjectionsOrErr.getVal();
                resultBuilder.putProjNetWorths(
                        scenarioId,
                        ValOrGerr.val(projectedNetWorths)
                );
            }
        }
        return resultBuilder.build();
    }

    /**
     * Parses an asset change date string which may be of the form "yyyy-mm-dd", or relative like
     *  "+5y", "+5m", etc.
     * @param text Text to parse
     * @return The parsed text, or an error if the text couldn't be parsed
     */
    private static ValOrGerr<LocalDate> parseChangeDateStr(String text) {
        try {
            return ValOrGerr.val(LocalDate.parse(text));
        } catch (DateTimeParseException e) {}

        Matcher matcher = RELATIVE_DATE_PATTERN.matcher(text);
        if (!matcher.find()) {
            return ValOrGerr.newGerr(
                    "Unable to parse relative date string '{}'",
                    text
            );
        }

        // Group 0 is the whole string
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
                return ValOrGerr.newGerr(
                        "Unrecognized relative date unit '{}'; this likely indicates a code error",
                        units
                );
        }
        return ValOrGerr.val(result);
    }

    private static ValOrGerr<SortedMap<LocalDate, Long>> calcScenarioNetWorthProjections(
            double defaultMonthlyMultiplier,
            ProjectionScenario projectionScenario,
            Map<String, Long> latestHistAssetValues,
            int projectionDisplayIncrementYears) {
        Set<LocalDate> assetChangeDates = new HashSet<>();
        Map<LocalDate, Map<String, AssetChange>> assetChangesByDate = new HashMap<>();
        for (Map.Entry<String, Map<String, AssetChange>> multiAssetChangesEntry : projectionScenario.getChanges().entrySet()) {
            String changeDateStr = multiAssetChangesEntry.getKey();
            Map<String, AssetChange> assetChanges = multiAssetChangesEntry.getValue();

            ValOrGerr<LocalDate> changeDateParseResult = parseChangeDateStr(changeDateStr);
            if (changeDateParseResult.hasGerr()) {
                return ValOrGerr.propGerr(
                        changeDateParseResult.getGerr(),
                        "An error occurred parsing change date string '{}'",
                        changeDateStr
                );
            }
            LocalDate changeDate = changeDateParseResult.getVal();
            assetChangeDates.add(changeDate);

            Map<String, AssetChange> assetChangesForDate = assetChangesByDate.getOrDefault(changeDate, new HashMap<>());
            for (Map.Entry<String, AssetChange> singleAssetChangeEntry : assetChanges.entrySet()) {
                String assetId = singleAssetChangeEntry.getKey();
                AssetChange change = singleAssetChangeEntry.getValue();

                assetChangesForDate.put(assetId, change);
                assetChangesByDate.put(changeDate, assetChangesForDate);
            }
        }

        Set<LocalDate> datesToLogNetWorth = new HashSet<>(assetChangesByDate.keySet());

        Set<LocalDate> compoundingDates = new HashSet<>();
        LocalDate today = LocalDate.now();
        for (int i = 0; i < MONTHS_IN_YEAR * MAX_YEARS_TO_PROJECT; i++) {
            LocalDate futureDate = today.plusMonths(i);
            compoundingDates.add(futureDate);
            if (i % (MONTHS_IN_YEAR * projectionDisplayIncrementYears) == 0) {
                datesToLogNetWorth.add(futureDate);
            }
        }

        Set<LocalDate> allDatesOfInterest = new HashSet<>();
        allDatesOfInterest.addAll(assetChangeDates);
        allDatesOfInterest.addAll(compoundingDates);

        List<LocalDate> orderedDatesOfInterest = allDatesOfInterest.stream()
                .sorted()
                .collect(Collectors.toList());

        Map<String, Long> currentAssetValues = new HashMap<>(latestHistAssetValues);
        SortedMap<LocalDate, Long> netWorthProjections = new TreeMap<>();
        for (LocalDate date : orderedDatesOfInterest) {
            // First apply any asset value changes
            if (assetChangeDates.contains(date)) {
                Map<String, AssetChange> assetChanges = assetChangesByDate.get(date);
                for (Map.Entry<String, AssetChange> assetChangeEntry : assetChanges.entrySet()) {
                    String assetId = assetChangeEntry.getKey();
                    AssetChange change = assetChangeEntry.getValue();

                    long oldValue = currentAssetValues.get(assetId);
                    ValOrGerr<Long> applicationResult = change.apply(oldValue);
                    if (applicationResult.hasGerr()) {
                        return ValOrGerr.propGerr(
                                applicationResult.getGerr(),
                                "An error occurred applying asset change from {} to asset with ID '{}'",
                                date,
                                assetId
                        );
                    }
                    long updatedValue = applicationResult.getVal();
                    currentAssetValues.put(assetId, updatedValue);
                }
            }

            // Apply monthly growth "interest" only if we're on the month boundary
            // This is so that the user defining an asset change doesn't cause a compounding (which would make
            //  their returns higher than they should be)
            if (compoundingDates.contains(date)) {
                for (String assetId : currentAssetValues.keySet()) {
                    Long currentValue = currentAssetValues.get(assetId);
                    long newValue = (long)(currentValue * defaultMonthlyMultiplier);
                    currentAssetValues.put(assetId, newValue);
                }
            }

            // Lastly, log the entry if it's a) a user-defined change or b) on our regular interval
            if (datesToLogNetWorth.contains(date)) {
                long netWorth = currentAssetValues.values().stream()
                        .reduce(0L, (l, r) -> l + r);
                netWorthProjections.put(date, netWorth);
            }
        }
        return ValOrGerr.val(netWorthProjections);
    }
}
