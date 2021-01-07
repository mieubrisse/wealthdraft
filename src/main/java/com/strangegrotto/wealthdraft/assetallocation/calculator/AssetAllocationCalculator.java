package com.strangegrotto.wealthdraft.assetallocation.calculator;

import com.jakewharton.picnic.CellStyle;
import com.jakewharton.picnic.Table;
import com.jakewharton.picnic.TableSection;
import com.jakewharton.picnic.TextAlignment;
import com.strangegrotto.wealthdraft.Display;
import com.strangegrotto.wealthdraft.assetallocation.AssetAllocationRenderer;
import com.strangegrotto.wealthdraft.assetallocation.TargetAssetAllocations;
import com.strangegrotto.wealthdraft.assetallocation.filters.AssetFilter;
import com.strangegrotto.wealthdraft.assets.definition.Asset;
import com.strangegrotto.wealthdraft.assets.temporal.AssetSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AssetAllocationCalculator {
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_EVEN;
    private static final int BIG_DECIMAL_DISPLAY_SCALE = 2;  // TODO replace this with something on Display

    // TODO Use these to color the output accordingly
    private final double deviationPercentageWarn;
    private final double deviationPercentageError;

    public AssetAllocationRenderer(double deviationPercentageWarn, double deviationPercentageError) {
        this.deviationPercentageWarn = deviationPercentageWarn;
        this.deviationPercentageError = deviationPercentageError;
    }

    public List<AssetAllocationCalcResult> calculateAssetAllocations(
            TargetAssetAllocations targetAssetAllocations,
            Map<String, Asset<?, ?>> assets,
            Map<String, AssetSnapshot<?>> latestAssetSnapshots) {
        var totalPortfolioValue = latestAssetSnapshots.values().stream()
                .map(AssetSnapshot::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        var filters = targetAssetAllocations.getFilters();
        var targets = targetAssetAllocations.getTargets();

        // GET TO DAH CHOPPAH!
        var results = new ArrayList<AssetAllocationCalcResult();
        for (var target : targets) {
            var numeratorFilterName = target.getNumeratorFilter();
            var numeratorFilter = filters.get(numeratorFilterName);
            var numeratorValue = getValueOfAssetsMatchingFilter(assets, latestAssetSnapshots, numeratorFilter);

            var denominatorFilterNameOpt = target.getDenominatorFilterOpt();
            BigDecimal denominatorValue;
            String denominatorStrRepr;
            if (denominatorFilterNameOpt.isPresent()) {
                var denominatorFilterName = denominatorFilterNameOpt.get();
                var denominatorFilter = filters.get(denominatorFilterName);
                denominatorValue = getValueOfAssetsMatchingFilter(assets, latestAssetSnapshots, denominatorFilter);
                denominatorStrRepr = denominatorFilterName;
            } else {
                denominatorValue = totalPortfolioValue;
                denominatorStrRepr = "Total Portfolio";
            }

            var calcResult = ImmAssetAllocationCalcResults.of(
                    numeratorFilterName,
                    denominatorStrRepr,

            )
            results.add()
            addTableRow(
                    tableBodyBuilder,
                    numeratorFilterName,
                    denominatorStrRepr,
                    numeratorValue,
                    denominatorValue,
                    target.getFraction()
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
        var matchingAssetIds = filter.apply(assets, assets.keySet());
        return latestAssetSnapshots.entrySet().stream()
                .filter(entry -> matchingAssetIds.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .map(AssetSnapshot::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
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
        var targetValue = targetFraction.multiply(denominatorValue).setScale(BIG_DECIMAL_DISPLAY_SCALE, RoundingMode.HALF_EVEN);
        var correctionNeeded = targetValue.subtract(numeratorValue).setScale(BIG_DECIMAL_DISPLAY_SCALE, RoundingMode.HALF_EVEN);

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
