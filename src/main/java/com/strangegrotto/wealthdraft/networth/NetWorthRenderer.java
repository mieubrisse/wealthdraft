package com.strangegrotto.wealthdraft.networth;

import com.strangegrotto.wealthdraft.Display;
import com.strangegrotto.wealthdraft.errors.ValOrGerr;
import com.strangegrotto.wealthdraft.networth.projections.AssetChange;
import com.strangegrotto.wealthdraft.networth.projections.ProjectionScenario;
import com.strangegrotto.wealthdraft.networth.projections.Projections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.*;

public class NetWorthRenderer {
    private static final int MONTHS_IN_YEAR = 12;

    private static final Logger log = LoggerFactory.getLogger(NetWorthRenderer.class);

    private final Display display;
    private final int projectionDisplayIncrementYears;
    private final int maxYearsToProject;

    public NetWorthRenderer(Display display, int projectionDisplayIncrementYears, int maxYearsToProject) {
        this.display = display;
        this.projectionDisplayIncrementYears = projectionDisplayIncrementYears;
        this.maxYearsToProject = maxYearsToProject;
    }

    public void renderNetWorthCalculations(AssetsWithHistory assetsWithHistory, Projections projections) {
        Map<String, Asset> assets = assetsWithHistory.getAssets();
        Map<String, Map<LocalDate, BankAccountAssetSnapshot>> history = assetsWithHistory.getHistory();

        display.printEmptyLine();
        display.printBannerHeader("Historical Net Worth");
        SortedMap<LocalDate, Map<String, BankAccountAssetSnapshot>> histAssetSnapshotsByDate = getHistAssetSnapshotsByDate(history);
        for (LocalDate date : histAssetSnapshotsByDate.keySet()) {
            Map<String, BankAccountAssetSnapshot> assetSnapshotsForDate = histAssetSnapshotsByDate.get(date);
            long netWorth = assetSnapshotsForDate.values().stream()
                    .map(AssetSnapshot::getValue)
                    .reduce(0L, (l, r) -> l + r);
            this.display.printCurrencyItem(date.toString(), netWorth);
        }

        Map<String, BankAccountAssetSnapshot> latestAssetSnapshots = histAssetSnapshotsByDate.get(histAssetSnapshotsByDate.lastKey());

        // TODO Change to AssetSnapshot (requires writing a deserializer for AssetsWithHistory)
        Map<String, AssetSnapshot> castedLatestAssetSnapshots = new HashMap<>();
        for (String assetId : latestAssetSnapshots.keySet()) {
            BankAccountAssetSnapshot bankAccountAssetSnapshot = latestAssetSnapshots.get(assetId);
            castedLatestAssetSnapshots.put(assetId, bankAccountAssetSnapshot);
        }

        Map<String, AssetType> assetTypes = new HashMap<>();
        assets.forEach((assetId, asset) -> assetTypes.put(assetId, asset.getType()));

        renderProjectionNetWorths(
                this.display,
                assetTypes,
                castedLatestAssetSnapshots,
                projections,
                this.maxYearsToProject,
                this.projectionDisplayIncrementYears
        );
    }

    private static SortedMap<LocalDate, Map<String, BankAccountAssetSnapshot>> getHistAssetSnapshotsByDate(Map<String, Map<LocalDate,BankAccountAssetSnapshot>> history) {
        SortedMap<LocalDate, Map<String, BankAccountAssetSnapshot>> assetSnapshotsByDate = new TreeMap<>();
        for (String assetId : history.keySet()) {
            Map<LocalDate, BankAccountAssetSnapshot> historyForAsset = history.get(assetId);
            for (LocalDate date : historyForAsset.keySet()) {
                BankAccountAssetSnapshot assetSnapshot = historyForAsset.get(date);
                Map<String, BankAccountAssetSnapshot> snapshotsOnDate = assetSnapshotsByDate.getOrDefault(date, new HashMap<>());
                snapshotsOnDate.put(assetId, assetSnapshot);
                assetSnapshotsByDate.put(date, snapshotsOnDate);
            }
        }

        // Because history can be declared piecemeal (e.g. asset A and B are declared on date T, asset B and C
        //  are declared on date T+1) we "fill forward" past snapshots into any slots in the future where
        //  they're missing. This assumes that asset values don't change over historical time.
        Map<String, BankAccountAssetSnapshot> latestAssetSnapshots = new HashMap<>();
        SortedMap<LocalDate, Map<String, BankAccountAssetSnapshot>> result = new TreeMap<>();
        for (LocalDate date : assetSnapshotsByDate.keySet()) {
            Map<String, BankAccountAssetSnapshot> assetSnapshotsForDate = assetSnapshotsByDate.get(date);
            latestAssetSnapshots.putAll(assetSnapshotsForDate);

            Map<String, BankAccountAssetSnapshot> resultAssetSnapshotsForDate = result.getOrDefault(date, new HashMap<>());
            resultAssetSnapshotsForDate.putAll(latestAssetSnapshots);
            result.put(date, resultAssetSnapshotsForDate);
        }
        return result;
    }

    private static ValOrGerr<Void> renderProjectionNetWorths(
            Display display,
            Map<String, AssetType> assetTypes,
            Map<String, AssetSnapshot> latestHistAssetSnapshots,
            Projections projections,
            int maxYearsToProject,
            int projectionDisplayIncrementYears) {
        var projectionsParseResults = projections.getScenarios();
        for (var scenarioId : projectionsParseResults.keySet()) {
            var scenarioParseResultOrErr = projectionsParseResults.get(scenarioId);

            display.printEmptyLine();
            display.printBannerHeader("Networth Proj: " + scenarioId);

            if (scenarioParseResultOrErr.hasGerr()) {
                log.error(scenarioParseResultOrErr.getGerr().toString());
                continue;
            }
            var scenario = scenarioParseResultOrErr.getVal();

            var futureNetWorthsOrErr = calculateSingleScenarioAssetSnapshots(
                    latestHistAssetSnapshots,
                    scenario,
                    maxYearsToProject,
                    projectionDisplayIncrementYears
            );
            if (futureNetWorthsOrErr.hasGerr()) {
                return ValOrGerr.propGerr(
                        futureNetWorthsOrErr.getGerr(),
                        "An error occurred calculating the future asset snapshots for the scenario"
                );
            }
            var futureNetWorths = futureNetWorthsOrErr.getVal();
            for (var date : futureNetWorths.keySet()) {
                var assetSnapshotsForDate = futureNetWorths.get(date);
                var netWorthOnDate = assetSnapshotsForDate.values().stream()
                        .map(snapshot -> snapshot.getValue())
                        .reduce(0L, (l, r) -> l + r);
                display.printCurrencyItem(date.toString(), netWorthOnDate);
            }
        }
        return ValOrGerr.val(null);
    }

    private static ValOrGerr<SortedMap<LocalDate, Map<String, AssetSnapshot>>> calculateSingleScenarioAssetSnapshots(
            Map<String, AssetSnapshot> latestHistAssetSnapshots,
            ProjectionScenario scenario,
            int maxYearsToProject,
            int projectionDisplayIncrementYears) {
        var assetChanges = scenario.getAssetChanges();
        var assetChangeDates = assetChanges.keySet();

        var datesToLogNetWorth = new HashSet<>(assetChanges.keySet());

        // Add dates to compound for growth
        LocalDate today = LocalDate.now();
        Set<LocalDate> compoundingDates = new HashSet<>();
        for (int i = 1; i < MONTHS_IN_YEAR * maxYearsToProject; i++) {
            LocalDate futureDate = today.plusMonths(i);
            compoundingDates.add(futureDate);
            if (i % (MONTHS_IN_YEAR * projectionDisplayIncrementYears) == 0) {
                datesToLogNetWorth.add(futureDate);
            }
        }

        SortedSet<LocalDate> allDatesOfInterest = new TreeSet<>();
        allDatesOfInterest.addAll(assetChangeDates);
        allDatesOfInterest.addAll(compoundingDates);

        // Loop through all dates of interest and
        Map<String, AssetSnapshot> currentAssetSnapshots = new HashMap<>(latestHistAssetSnapshots);
        SortedMap<LocalDate, Map<String, AssetSnapshot>> projectedSnapshots = new TreeMap<>();
        for (LocalDate date : allDatesOfInterest) {
            Map<String, AssetSnapshot> newAssetSnapshots = new HashMap<>(currentAssetSnapshots);

            // Apply monthly growth only if we're on the month boundary
            // This is so that the user defining an asset change doesn't cause a compounding (which would make
            //  their returns higher than they should be)
            if (compoundingDates.contains(date)) {
                for (String assetId : newAssetSnapshots.keySet()) {
                    AssetSnapshot snapshot = newAssetSnapshots.get(assetId);
                    // TODO to be entirely accurate, this should really be daily compounding (since the user could issue
                    //  a +15k to their bank account the day before compounding and that would all be counted)
                    AssetSnapshot newSnapshot = snapshot.projectOneMonth();
                    newAssetSnapshots.put(assetId, newSnapshot);
                }
            }

            // Apply manual asset changes for the date only after compounding
            if (assetChangeDates.contains(date)) {
                Map<String, AssetChange> assetChangesForDate = assetChanges.get(date);

                for (String assetId : assetChangesForDate.keySet()) {
                    AssetChange change = assetChangesForDate.get(assetId);
                    AssetSnapshot snapshot = newAssetSnapshots.get(assetId);
                    ValOrGerr<AssetSnapshot> newSnapshotOrErr = snapshot.applyChange(change);
                    if (newSnapshotOrErr.hasGerr()) {
                        return ValOrGerr.propGerr(
                                newSnapshotOrErr.getGerr(),
                                "An error occurred applying change to asset {} on {}",
                                assetId,
                                date
                        );
                    }
                    AssetSnapshot newSnapshot = newSnapshotOrErr.getVal();
                    newAssetSnapshots.put(assetId, newSnapshot);
                }
            }

            projectedSnapshots.put(date, newAssetSnapshots);
            currentAssetSnapshots = newAssetSnapshots;
        }
        return ValOrGerr.val(projectedSnapshots);
    }

    /*
    // TODO write tests for this!!
    public ProjNetWorthCalcResults parseManualAssetChanges(Map<String, Long> latestHistAssetValues, Projections projections) {
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
            for (Map.Entry<String, Map<String, AssetParameterChange>> changesForDateEntry : scenario.getChanges().entrySet()) {
                String dateStr = changesForDateEntry.getKey();
                Map<String, AssetParameterChange> changes = changesForDateEntry.getValue();

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
    */
}
