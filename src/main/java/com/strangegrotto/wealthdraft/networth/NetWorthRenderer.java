package com.strangegrotto.wealthdraft.networth;

import com.strangegrotto.wealthdraft.Display;
import com.strangegrotto.wealthdraft.errors.ValOrGerr;
import com.strangegrotto.wealthdraft.networth.projections.AssetChange;
import com.strangegrotto.wealthdraft.networth.projections.ProjectionScenario;
import com.strangegrotto.wealthdraft.networth.projections.Projections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
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
            var netWorth = assetSnapshotsForDate.values().stream()
                    .map(AssetSnapshot::getValue)
                    .reduce(BigDecimal.ZERO, (l, r) -> l.add(r));
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
                        .reduce(BigDecimal.ZERO, (l, r) -> l.add(r));
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

        var result = new TreeMap<LocalDate, Map<String, AssetSnapshot>>();
        for (var date : datesToLogNetWorth) {
            result.put(date, projectedSnapshots.get(date));
        }

        return ValOrGerr.val(result);
    }
}
