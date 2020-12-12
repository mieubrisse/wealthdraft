package com.strangegrotto.wealthdraft.networth.projections;

import com.google.common.annotations.VisibleForTesting;
import com.strangegrotto.wealthdraft.errors.ValOrGerr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

// TODO Rename to parser
public class ProjNetWorthCalculator {
    private static final int MAX_YEARS_TO_PROJECT = 80;

    private static final int MONTHS_IN_YEAR = 12;

    private static final Logger log = LoggerFactory.getLogger(ProjNetWorthCalculator.class);

    // Type aliases for making working with these nested maps less cumbersome
    @VisibleForTesting
    static class AssetChangesForDate extends HashMap<String, LinkedList<AssetParameterChange>> { }

    @VisibleForTesting
    static class AssetChangesForScenario extends HashMap<LocalDate, AssetChangesForDate> {}

    // TODO write tests for this!!
    public static Map<String, ValOrGerr<ProjScenarioParseResult>> parseManualAssetChanges(Projections projections) {
        ImmutableProjScenarioParseResult.Builder resultBuilder = ImmutableProjScenarioParseResult.builder();

        Map<String, ProjectionScenario> allScenarios = projections.getScenarios();
        for (String scenarioId : allScenarios.keySet()) {
            ValOrGerr<ProjScenarioParseResult> netWorthProjectionsOrErr = parseSingleScenario(
                    scenarioId,
                    allScenarios);
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

    private static ValOrGerr<ProjScenarioParseResult> parseSingleScenario(
            String scenarioId,
            Map<String, ProjectionScenario> allScenarios) {
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
                for (Map.Entry<String, LinkedList<AssetParameterChange>> assetChangeEntry : assetChangesForDate.entrySet()) {
                    String assetId = assetChangeEntry.getKey();
                    LinkedList<AssetParameterChange> changes = assetChangeEntry.getValue();

                    // Scenarios can be based on other scenarios. The iteration order here will be the most-upstream
                    //  dependency scenario asset changes first, followed by changes from any scenarios that depend on it,
                    //  followed by changes from any scenarios that depend on that, etc.
                    for (int i = 0; i < changes.size(); i++) {
                        AssetParameterChange change = changes.get(i);
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
    static ValOrGerr<Map<RelativeLocalDate, AssetParameterChange>> unrollAssetChanges(
            String scenarioId,
            Map<String, ProjectionScenario> allScenarios) {
        // For scenarios based on other ones, we need to put those guys in
        Map<RelativeLocalDate, List<AssetParameterChange>> result = new HashMap<>();
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
            Map<RelativeLocalDate, Map<String, AssetParameterChange>> scenarioAssetChanges = scenario.getRawChanges();
            for (RelativeLocalDate date : scenarioAssetChanges.keySet()) {
                Map<String, AssetParameterChange> changes = scenarioAssetChanges.get(date);

                Map<RelativeLocalDate, AssetParameterChange> resultChangesOnDate
                        = result.getOrDefault(date, new HashMap<RelativeLocalDate, AssetParameterChange>());
                for (Map.Entry<String, AssetParameterChange> assetChangeEntry : changes.entrySet()) {
                    String assetId = assetChangeEntry.getKey();
                    AssetParameterChange change = assetChangeEntry.getValue();

                    // We push the element to the FRONT of the list since we're iterating scenario dependencies in
                    //  downstream-to-upstream order, but we want the changes to be applied in upstream-to-downstream precedence
                    LinkedList<AssetParameterChange> resultChangesList = resultChangesOnDate.getOrDefault(assetId, new LinkedList<>());
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
