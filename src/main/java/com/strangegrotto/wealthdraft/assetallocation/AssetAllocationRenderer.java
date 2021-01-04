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
        tableBodyBuilder.addRow("Selectors", "Current Value", "Current % Portfolio", "Desired Value", "Desired % Portfolio", "Change Needed");
        for (var targetAllocation : targetAssetAllocations) {
            var filter = targetAllocation.getFilter();
            var matchingAssetIds = filter.apply(assets).keySet();
            var matchingAssetValue = latestAssetSnapshots.entrySet().stream()
                    .filter(entry -> matchingAssetIds.contains(entry.getKey()))
                    .map(Map.Entry::getValue)
                    .map(AssetSnapshot::getValue)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            addTableRow(
                    tableBodyBuilder,
                    filter.getStringRepresentation(),
                    matchingAssetValue,
                    targetAllocation.getPortfolioFraction(),
                    totalPortfolioValue
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

    private static void addTableRow(
            TableSection.Builder tableBodyBuilder,
            String selectors,
            BigDecimal currentValue,
            BigDecimal targetPortfolioFraction,
            BigDecimal totalPortfolioValue) {
        var currentValuePortfolioFraction = currentValue.divide(
                totalPortfolioValue,
                BIG_DECIMAL_DISPLAY_SCALE + 2,  // Plus 2 so that when we multiply by 100 to get a fraction we have scale == 2
                RoundingMode.HALF_EVEN);
        var currentValuePortfolioPercent = formatFractionAsPercent(currentValuePortfolioFraction);
        var targetPortfolioPercent = formatFractionAsPercent(targetPortfolioFraction);
        var targetValue = targetPortfolioFraction.multiply(totalPortfolioValue).setScale(BIG_DECIMAL_DISPLAY_SCALE);
        var correctionNeeded = targetValue.subtract(currentValue).setScale(BIG_DECIMAL_DISPLAY_SCALE);

        // TODO Pull back a preferred currency format and use that here instead!
        var strRow = List.of(
                selectors,
                currentValue.toString(),
                currentValuePortfolioPercent.toString(),
                targetValue.toString(),
                targetPortfolioPercent.toString(),
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

