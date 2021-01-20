package com.strangegrotto.wealthdraft.frontend;

import com.strangegrotto.wealthdraft.Display;
import com.strangegrotto.wealthdraft.backend.assethistory.impl.SerAssetsHistory;
import com.strangegrotto.wealthdraft.errors.ValOrGerr;
import com.strangegrotto.wealthdraft.backend.assethistory.api.types.AssetSnapshot;
import com.strangegrotto.wealthdraft.backend.projections.impl.SerProjections;
import com.strangegrotto.wealthdraft.backend.projections.impl.SerProjectionScenario;
import com.strangegrotto.wealthdraft.backend.projections.impl.temporal.AssetChange;
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

    public ValOrGerr<Void> renderNetWorthCalculations(SerAssetsHistory assetsHistory, SerProjections projections) {
        SortedMap<LocalDate, Map<String, AssetSnapshot<?>>> histAssetSnapshotsByDate = assetsHistory.getHistory();
        display.printEmptyLine();
        display.printBannerHeader("Historical Net Worth");
        histAssetSnapshotsByDate.forEach((date, assetSnapshotsForDate) -> {
            var netWorth = assetSnapshotsForDate.values().stream()
                    .map(AssetSnapshot::getValue)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            this.display.printCurrencyItem(date.toString(), netWorth);
        });

        var latestAssetSnapshots = histAssetSnapshotsByDate.get(histAssetSnapshotsByDate.lastKey());
        var emptyOrErr = renderProjectionNetWorths(
                this.display,
                latestAssetSnapshots,
                projections,
                this.maxYearsToProject,
                this.projectionDisplayIncrementYears
        );
        if (emptyOrErr.hasGerr()) {
            return ValOrGerr.propGerr(
                    emptyOrErr.getGerr(),
                    "An error occurred rendering the net worth projection scenarios"
            );
        }
        return ValOrGerr.val(null);
    }

    private static ValOrGerr<Void> renderProjectionNetWorths(
            Display display,
            Map<String, AssetSnapshot<?>> latestHistAssetSnapshots,
            SerProjections projections,
            int maxYearsToProject,
            int projectionDisplayIncrementYears) {
        var projectionsParseResults = projections.getScenarios();
        for (var entry : projectionsParseResults.entrySet()) {
            var scenarioId = entry.getKey();
            var scenarioParseResultOrErr = entry.getValue();

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
            futureNetWorths.forEach((date, assetSnapshotsForDate) -> {
                var netWorthOnDate = assetSnapshotsForDate.values().stream()
                        .map(AssetSnapshot::getValue)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                display.printCurrencyItem(date.toString(), netWorthOnDate);
            });
        }
        return ValOrGerr.val(null);
    }

    private static ValOrGerr<SortedMap<LocalDate, Map<String, AssetSnapshot<?>>>> calculateSingleScenarioAssetSnapshots(
            Map<String, AssetSnapshot<?>> latestHistAssetSnapshots,
            SerProjectionScenario scenario,
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

        Map<String, AssetSnapshot<?>> currentAssetSnapshots = new HashMap<>(latestHistAssetSnapshots);
        SortedMap<LocalDate, Map<String, AssetSnapshot<?>>> projectedSnapshots = new TreeMap<>();
        for (LocalDate date : allDatesOfInterest) {
            Map<String, AssetSnapshot<?>> newAssetSnapshots = new HashMap<>(currentAssetSnapshots);

            // Apply monthly growth only if we're on the month boundary
            // This is so that the user defining an asset change doesn't cause a compounding (which would make
            //  their returns higher than they should be)
            if (compoundingDates.contains(date)) {
                for (var assetSnapshotEntry : newAssetSnapshots.entrySet()) {
                    var assetId = assetSnapshotEntry.getKey();
                    AssetSnapshot<?> snapshot = assetSnapshotEntry.getValue();
                    // TODO to be entirely accurate, this should really be daily compounding (since the user could issue
                    //  a +15k to their bank account the day before compounding and that would all be counted)
                    AssetSnapshot<?> newSnapshot = snapshot.projectOneMonth();
                    newAssetSnapshots.put(assetId, newSnapshot);
                }
            }

            // Apply manual asset changes for the date only after compounding
            if (assetChangeDates.contains(date)) {
                Map<String, AssetChange> assetChangesForDate = assetChanges.get(date);

                for (var entry : assetChangesForDate.entrySet()) {
                    var assetId = entry.getKey();
                    var change = entry.getValue();
                    AssetSnapshot<?> snapshot = newAssetSnapshots.get(assetId);
                    ValOrGerr<? extends AssetSnapshot<?>> newSnapshotOrErr = snapshot.applyChange(change);
                    if (newSnapshotOrErr.hasGerr()) {
                        return ValOrGerr.propGerr(
                                newSnapshotOrErr.getGerr(),
                                "An error occurred applying change to asset {} on {}",
                                assetId,
                                date
                        );
                    }
                    AssetSnapshot<?> newSnapshot = newSnapshotOrErr.getVal();
                    newAssetSnapshots.put(assetId, newSnapshot);
                }
            }

            projectedSnapshots.put(date, newAssetSnapshots);
            currentAssetSnapshots = newAssetSnapshots;
        }

        var result = new TreeMap<LocalDate, Map<String, AssetSnapshot<?>>>();
        for (var date : datesToLogNetWorth) {
            result.put(date, projectedSnapshots.get(date));
        }

        return ValOrGerr.val(result);
    }
}
