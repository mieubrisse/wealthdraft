package com.strangegrotto.wealthdraft.assetallocation;

import com.strangegrotto.wealthdraft.Display;
import com.strangegrotto.wealthdraft.assets.definition.Asset;
import com.strangegrotto.wealthdraft.assets.temporal.AssetSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

// TODO Convert this, and all the other calculator-renderers (e.g. NetWorthRenderer) to return POJOs so that
//  we split up the calculation & rendering step (and can write good unit tests)
public class AssetAllocationRenderer {
    private static final Logger log = LoggerFactory.getLogger(AssetAllocationRenderer.class);

    private final Display display;
    private final double deviationPercentageWarn;
    private final double deviationPercentageError;

    public AssetAllocationRenderer(Display display, double deviationPercentageWarn, double deviationPercentageError) {
        this.display = display;
        this.deviationPercentageWarn = deviationPercentageWarn;
        this.deviationPercentageError = deviationPercentageError;
    }

    public void renderAssetAllocations(
            List<TargetAssetAllocation> targetAssetAllocations,
            Map<String, Asset<?, ?>> assets,
            Map<String, AssetSnapshot<?>> latestAssetSnapshots) {
        display.printEmptyLine();
        display.printBannerHeader("Asset Allocations");
        var totalPortfolioWorth = latestAssetSnapshots.values().stream()
                .map(AssetSnapshot::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        for (var targetAllocation : targetAssetAllocations) {
            var filter = targetAllocation.getFilter();
            var matchingAssetIds = filter.apply(assets).keySet();
            var matchingAssetValue = latestAssetSnapshots.entrySet().stream()
                    .filter(entry -> matchingAssetIds.contains(entry.getKey()))
                    .map(Map.Entry::getValue)
                    .map(AssetSnapshot::getValue)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            var matchingAssetPortfolioPercent = matchingAssetValue.divide(totalPortfolioWorth, 4, RoundingMode.HALF_EVEN);
            var targetPortfolioPercent = targetAllocation.getPercentagePortfolio();
            var targetValue = targetPortfolioPercent.multiply(totalPortfolioWorth);
            var correctionNeeded = targetValue.subtract(matchingAssetValue);

            // TODO Super janky - replace with a better way of doing this!!
            var formattedStr = String.format(
                    // The double percent is the way to escape the percent sign
                    "Current: %s (%s%% portfolio), Desired: %s (%s%% portfolio), Change needed: %s",
                    matchingAssetValue.setScale(2, RoundingMode.HALF_EVEN),
                    formatBigDecimalAsPercent(matchingAssetPortfolioPercent),
                    targetValue.setScale(2, RoundingMode.HALF_EVEN),
                    formatBigDecimalAsPercent(targetPortfolioPercent),
                    correctionNeeded.setScale(2, RoundingMode.HALF_EVEN)
            );
            display.printStringItem(
                    filter.getStringRepresentation(),
                    formattedStr
            );
        }
    }

    private BigDecimal formatBigDecimalAsPercent(BigDecimal input) {
        var scaledUp = input.multiply(BigDecimal.valueOf(100));
        return scaledUp.setScale(2, RoundingMode.HALF_EVEN);
    }
}

