package com.strangegrotto.wealthdraft.networth.historical;

import com.strangegrotto.wealthdraft.errors.ValueOrGError;
import com.strangegrotto.wealthdraft.validator.ValidationWarning;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class HistNetWorthCalculator {
    private static final Logger log = LoggerFactory.getLogger(HistNetWorthCalculator.class);

    private final long staleAssetThresholdDays;

    public HistNetWorthCalculator(long staleAssetThresholdDays) {
        this.staleAssetThresholdDays = staleAssetThresholdDays;
    }

    public ValueOrGError<HistNetWorthCalcResults> calculateHistoricalNetWorth(
            Map<String, Asset> assets) {
        ImmutableHistNetWorthCalcResults.Builder resultBuilder = ImmutableHistNetWorthCalcResults.builder();

        ValueOrGError<List<ValidationWarning>> assetValidationResult = validateAssets(assets);
        if (assetValidationResult.hasError()) {
            log.error(assetValidationResult.getError().toString());
            return ValueOrGError.ofPropagatedErr(
                    assetValidationResult.getError(),
                    "An error occurred validating assets"
            );
        }
        resultBuilder.addAllValidationWarnings(assetValidationResult.getValue());

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
        SortedMap<LocalDate, Long> historicalNetWorth = new TreeMap<>();
        for (LocalDate date : histNetWorthCheckpointDates) {
            Map<String, Long> assetValuesAtCheckpoint = histNetWorthCheckpointValues.get(date);
            latestHistAssetValues.putAll(assetValuesAtCheckpoint);
            long netWorthAtCheckpoint = latestHistAssetValues.values().stream()
                    .reduce(0L, (l, r) -> l + r);
            historicalNetWorth.put(date, netWorthAtCheckpoint);
        }
        resultBuilder.latestAssetValues(latestHistAssetValues);
        resultBuilder.historicalNetWorth(historicalNetWorth);

        return ValueOrGError.ofValue(resultBuilder.build());
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
}
