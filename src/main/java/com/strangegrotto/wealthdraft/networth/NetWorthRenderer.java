package com.strangegrotto.wealthdraft.networth;

import com.strangegrotto.wealthdraft.Display;
import com.strangegrotto.wealthdraft.errors.ValOrGerr;
import com.strangegrotto.wealthdraft.networth.projections.ProjNetWorthCalcResults;
import com.strangegrotto.wealthdraft.networth.projections.ProjNetWorthCalculator;
import com.strangegrotto.wealthdraft.networth.projections.ProjectionScenario;
import com.strangegrotto.wealthdraft.networth.projections.Projections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class NetWorthRenderer {
    private static final Logger log = LoggerFactory.getLogger(NetWorthRenderer.class);

    private final Display display;
    private final int projectionDisplayIncrementYears;

    public NetWorthRenderer(Display display, int projectionDisplayIncrementYears) {
        this.display = display;
        this.projectionDisplayIncrementYears = projectionDisplayIncrementYears;
    }

    public void renderNetWorthCalculations(AssetsWithHistory assetsWithHistory, Projections projections) {
        display.printEmptyLine();
        display.printBannerHeader("Historical Net Worth");
        Map<String, Map<LocalDate, BankAccountAssetSnapshot>> history = assetsWithHistory.getHistory();

        Map<String, BankAccountAssetSnapshot> latestAssetSnapshots = calculateAndRenderHistoricalNetWorth(display, history);

        // TODO Need to upgrade the projection calculator too!
        Map<String, Long> latestAssetValues = new HashMap<>();
        latestAssetSnapshots.forEach((assetId, snapshot) -> latestAssetValues.put(assetId, snapshot.getValue()));

        ProjNetWorthCalculator projNetWorthCalculator = new ProjNetWorthCalculator(this.projectionDisplayIncrementYears);
        ProjNetWorthCalcResults projNetWorthCalcResults = projNetWorthCalculator.calculateNetWorthProjections(
                latestAssetValues,
                projections
        );

        for (Map.Entry<String, ValOrGerr<SortedMap<LocalDate, Long>>> scenarioCalcEntry
                : projNetWorthCalcResults.getProjNetWorths().entrySet()) {
            String projScenarioId = scenarioCalcEntry.getKey();
            ValOrGerr<SortedMap<LocalDate, Long>> netWorthProjectionsOrErr = scenarioCalcEntry.getValue();
            ProjectionScenario projScenario = projections.getScenarios().get(projScenarioId);
            String projScenarioName = projScenario.getName();

            log.debug("Processing scenario projection results for ID '{}' and name '{}'", projScenarioId, projScenarioName);

            display.printEmptyLine();
            display.printBannerHeader("Networth Proj: " + projScenarioName);

            if (netWorthProjectionsOrErr.hasGerr()) {
                log.error(netWorthProjectionsOrErr.getGerr().toString());
                continue;
            }
            Map<LocalDate, Long> netWorthProjections = netWorthProjectionsOrErr.getVal();
            netWorthProjections.forEach((date, netWorth) -> {
                display.printCurrencyItem(date.toString(), netWorth);
            });
        }
    }

    private static Map<String, BankAccountAssetSnapshot> calculateAndRenderHistoricalNetWorth(
            Display display,
            Map<String, Map<LocalDate,BankAccountAssetSnapshot>> history) {
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
        // SortedMap<LocalDate, Long> historicalNetWorth = new TreeMap<>();
        for (LocalDate date : assetSnapshotsByDate.keySet()) {
            Map<String, BankAccountAssetSnapshot> assetSnapshotsForDate = assetSnapshotsByDate.get(date);
            latestAssetSnapshots.putAll(assetSnapshotsForDate);
            long netWorth = latestAssetSnapshots.values().stream()
                    .map(snapshot -> snapshot.getValue())
                    .reduce(0L, (l, r) -> l + r);
            // TODO this is an awful spot for this
            display.printCurrencyItem(date.toString(), netWorth);
        }
        return latestAssetSnapshots;
    }
}
