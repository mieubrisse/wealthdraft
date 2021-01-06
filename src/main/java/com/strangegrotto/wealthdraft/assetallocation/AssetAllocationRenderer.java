package com.strangegrotto.wealthdraft.assetallocation;

import com.jakewharton.picnic.CellStyle;
import com.jakewharton.picnic.Table;
import com.jakewharton.picnic.TableSection;
import com.jakewharton.picnic.TextAlignment;
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
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_EVEN;
    private static final int BIG_DECIMAL_DISPLAY_SCALE = 2;  // TODO replace this with something on Display

    private final Display display;

    // TODO Use these to color the output accordingly
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
        var totalPortfolioValue = latestAssetSnapshots.values().stream()
                .map(AssetSnapshot::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // GET TO DAH CHOPPAH!
        var tableBodyBuilder = new TableSection.Builder();
        tableBodyBuilder.addRow("Numerator Selectors", "Denominator Selectors", "Current Num/Denom %", "Desired Num/Denom %", "Change Needed");
        for (var targetAllocation : targetAssetAllocations) {
            var numeratorFilter = targetAllocation.getNumeratorFilter();
            var numeratorValue = getValueOfAssetsMatchingFilter(assets, latestAssetSnapshots, numeratorFilter);

            var denominatorFilterOpt = targetAllocation.getDenominatorOpt();
            BigDecimal denominatorValue;
            String denominatorSelectors;
            if (denominatorFilterOpt.isPresent()) {
                var denominatorFilter = denominatorFilterOpt.get();
                denominatorValue = getValueOfAssetsMatchingFilter(assets, latestAssetSnapshots, denominatorFilter);
                denominatorSelectors = denominatorFilter.getStringRepresentation();
            } else {
                denominatorValue = totalPortfolioValue;
                denominatorSelectors = "Total Portfolio";
            }

            addTableRow(
                    tableBodyBuilder,
                    numeratorFilter.getStringRepresentation(),
                    denominatorSelectors,
                    numeratorValue,
                    denominatorValue,
                    targetAllocation.getFraction()
            );
        }

        // TODO Replace this with a better method inside Display
        var tableBody = tableBodyBuilder.build();
        var cellStyle = new CellStyle.Builder()
                .setAlignment(TextAlignment.MiddleCenter)
                .setPadding(1)
                .setBorder(true)
                .build();
        var table = new Table.Builder()
                .setCellStyle(cellStyle)
                .setBody(tableBody)
                .build();
        System.out.println(table.toString());
    }

    private static BigDecimal getValueOfAssetsMatchingFilter(
            Map<String, Asset<?, ?>> assets,
            Map<String, AssetSnapshot<?>> latestAssetSnapshots,
            AssetFilter filter) {
        var matchingAssetIds = filter.apply(assets).keySet();
        var matchingAssetValue = latestAssetSnapshots.entrySet().stream()
                .filter(entry -> matchingAssetIds.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .map(AssetSnapshot::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return matchingAssetValue;
    }

    private static void addTableRow(
            TableSection.Builder tableBodyBuilder,
            String numeratorSelectors,
            String denominatorSelectors,
            BigDecimal numeratorValue,
            BigDecimal denominatorValue,
            BigDecimal targetFraction) {
        var currentFraction = numeratorValue.divide(
                denominatorValue,
                BIG_DECIMAL_DISPLAY_SCALE + 2,  // Plus 2 so that when we multiply by 100 to get a fraction we have scale == 2
                RoundingMode.HALF_EVEN);
        var currentPercent = formatFractionAsPercent(currentFraction);
        var targetPercent = formatFractionAsPercent(targetFraction);
        var targetValue = targetFraction.multiply(denominatorValue).setScale(BIG_DECIMAL_DISPLAY_SCALE);
        var correctionNeeded = targetValue.subtract(numeratorValue).setScale(BIG_DECIMAL_DISPLAY_SCALE);

        // TODO Pull back a preferred currency format and use that here instead!
        var strRow = List.of(
                numeratorSelectors,
                denominatorSelectors,
                currentPercent.toString(),
                targetPercent.toString(),
                correctionNeeded.toString()
        );
        var strRowArr = strRow.toArray(new String[0]);
        tableBodyBuilder.addRow(strRowArr);
    }

    private static BigDecimal formatFractionAsPercent(BigDecimal input) {
        var scaledUp = input.multiply(BigDecimal.valueOf(100));
        return scaledUp.setScale(BIG_DECIMAL_DISPLAY_SCALE, ROUNDING_MODE);
    }
}

