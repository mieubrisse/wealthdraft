package com.strangegrotto.wealthdraft.networth.projections;

import com.google.common.annotations.VisibleForTesting;
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

    // Type aliases for making working with these nested maps less cumbersome
    @VisibleForTesting
    static class AssetChangesForDate extends HashMap<String, LinkedList<AssetChange>> { }

    @VisibleForTesting
    static class AssetChangesForScenario extends HashMap<LocalDate, AssetChangesForDate> {}

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

        Map<String, ProjectionScenario> allScenarios = projections.getScenarios();
        for (String scenarioId : allScenarios.keySet()) {
            ValOrGerr<SortedMap<LocalDate, Long>> netWorthProjectionsOrErr = calcScenarioNetWorthProjections(
                    scenarioId,
                    allScenarios,
                    latestHistAssetValues,
                    defaultMonthlyMultiplier,
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
            String scenarioId,
            Map<String, ProjectionScenario> allScenarios,
            Map<String, Long> latestHistAssetValues,
            double defaultMonthlyMultiplier,
            int projectionDisplayIncrementYears) {
        LocalDate today = LocalDate.now();

        ValOrGerr<AssetChangesForScenario> assetChangesByDateOrErr = unrollAssetChanges(scenarioId, allScenarios);
        if (assetChangesByDateOrErr.hasGerr()) {
            return ValOrGerr.propGerr(
                    assetChangesByDateOrErr.getGerr(),
                    "An error occurred unrolling the scenario's asset change dependencies for scenario with ID '{}'",
                    scenarioId
            );
        }
        AssetChangesForScenario assetChangesByDate = assetChangesByDateOrErr.getVal();
        Set<LocalDate> assetChangeDates = assetChangesByDate.keySet();

        // Validate that no asset change dates are in the past, else the scenario is invalid
        for (LocalDate changeDate : assetChangesByDate.keySet()) {
            if (today.isAfter(changeDate)) {
                return ValOrGerr.newGerr(
                        "Scenario with ID '{}' cannot be calculated because it uses date '{}' which is in the past",
                        scenarioId,
                        changeDate
                );
            }
        }

        Set<LocalDate> datesToLogNetWorth = new HashSet<>(assetChangeDates);
        Set<LocalDate> compoundingDates = new HashSet<>();
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
                AssetChangesForDate assetChangesForDate = assetChangesByDate.get(date);
                for (Map.Entry<String, LinkedList<AssetChange>> assetChangeEntry : assetChangesForDate.entrySet()) {
                    String assetId = assetChangeEntry.getKey();
                    LinkedList<AssetChange> changes = assetChangeEntry.getValue();

                    // Scenarios can be based on other scenarios. The iteration order here will be the most-upstream
                    //  dependency scenario asset changes first, followed by changes from any scenarios that depend on it,
                    //  followed by changes from any scenarios that depend on that, etc.
                    for (int i = 0; i < changes.size(); i++) {
                        AssetChange change = changes.get(i);
                        long oldValue = currentAssetValues.get(assetId);
                        ValOrGerr<Long> applicationResult = change.apply(oldValue);
                        if (applicationResult.hasGerr()) {
                            return ValOrGerr.propGerr(
                                    applicationResult.getGerr(),
                                    "An error occurred applying asset change #{} from {} to asset with ID '{}'",
                                    i,
                                    date,
                                    assetId
                            );
                        }
                        long updatedValue = applicationResult.getVal();
                        currentAssetValues.put(assetId, updatedValue);
                    }
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

    /**
     * For scenarios that are based on other scenarios, recursively unrolls the asset changes all the way
     *  through the dependency tree.
     */
    @VisibleForTesting
    static ValOrGerr<AssetChangesForScenario> unrollAssetChanges(
            String scenarioId, Map<String,
            ProjectionScenario> allScenarios) {
        // For scenarios based on other ones, we need to put those guys in
        AssetChangesForScenario result = new AssetChangesForScenario();
        Optional<String> baseIdOpt = Optional.of(scenarioId);
        Set<String> visitedScenarioIds = new HashSet<>();
        while (baseIdOpt.isPresent()) {
            String baseId = baseIdOpt.get();
            if (visitedScenarioIds.contains(baseId)) {
                return ValOrGerr.newGerr(
                        "Scenario '{}' has a dependency cycle, with scenario '{}' visited twice",
                        scenarioId,
                        baseId
                );
            }
            visitedScenarioIds.add(baseId);

            ProjectionScenario scenario = allScenarios.get(baseId);
            for (Map.Entry<String, Map<String, AssetChange>> changesForDateEntry : scenario.getChanges().entrySet()) {
                String dateStr = changesForDateEntry.getKey();
                Map<String, AssetChange> changes = changesForDateEntry.getValue();

                ValOrGerr<LocalDate> dateOrErr = parseChangeDateStr(dateStr);
                if (dateOrErr.hasGerr()) {
                    return ValOrGerr.propGerr(
                            dateOrErr.getGerr(),
                            "An error occurred parsing change date string '{}' for scenario with ID '{}'",
                            dateStr,
                            baseId
                    );
                }
                LocalDate date = dateOrErr.getVal();

                AssetChangesForDate resultChangesOnDate = result.getOrDefault(date, new AssetChangesForDate());
                for (Map.Entry<String, AssetChange> assetChangeEntry : changes.entrySet()) {
                    String assetId = assetChangeEntry.getKey();
                    AssetChange change = assetChangeEntry.getValue();

                    // We push the element to the FRONT of the list since we're iterating scenario dependencies in
                    //  downstream-to-upstream order, but we want the changes to be applied in upstream-to-downstream precedence
                    LinkedList<AssetChange> resultChangesList = resultChangesOnDate.getOrDefault(assetId, new LinkedList<>());
                    resultChangesList.push(change);
                    resultChangesOnDate.put(assetId, resultChangesList);
                }
                result.put(date, resultChangesOnDate);
            }
            baseIdOpt = scenario.getBase();
        }
        return ValOrGerr.val(result);
    }
}
