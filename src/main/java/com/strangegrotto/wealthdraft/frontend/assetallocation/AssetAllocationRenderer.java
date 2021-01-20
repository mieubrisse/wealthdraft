package com.strangegrotto.wealthdraft.frontend.assetallocation;

import com.strangegrotto.wealthdraft.Display;
import com.strangegrotto.wealthdraft.backend.assetallocation.api.TargetAssetAllocationsStore;
import com.strangegrotto.wealthdraft.backend.assetallocation.api.types.TargetAssetAllocation;
import com.strangegrotto.wealthdraft.backend.assetallocationcalc.api.AssetAllocationCalculator;
import com.strangegrotto.wealthdraft.backend.assetallocationcalc.api.types.AssetAllocationCalcResult;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;

// TODO Convert this, and all the other calculator-renderers (e.g. NetWorthRenderer) to return POJOs so that
//  we split up the calculation & rendering step (and can write good unit tests)
public class AssetAllocationRenderer {
    // TODO Replace both of these with some sort of mathematical constant supplier, so config is centralized
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_EVEN;
    private static final int BIG_DECIMAL_DISPLAY_SCALE = 2;  // TODO replace this with something on Display

    // The string displayed when the denominator isn't specified and we're using the whole portfolio
    private static final String TOTAL_PORTFOLIO_STR = "Total Portfolio";

    private final Display display;
    private final TargetAssetAllocationsStore targetAllocationStore;
    private final AssetAllocationCalculator assetAllocationCalculator;

    public AssetAllocationRenderer(Display display, TargetAssetAllocationsStore targetAllocationStore, AssetAllocationCalculator assetAllocationCalculator) {
        this.display = display;
        this.targetAllocationStore = targetAllocationStore;
        this.assetAllocationCalculator = assetAllocationCalculator;
    }

    public void render() {
        display.printEmptyLine();
        display.printBannerHeader("Asset Allocations");

        var targetAssetAllocations = this.targetAllocationStore.getTargetAllocations();
        var calcResults = new LinkedHashMap<TargetAssetAllocation, AssetAllocationCalcResult>();
        for (var targetAllocation : this.targetAllocationStore.getTargetAllocations()) {
            var calcResult = this.assetAllocationCalculator.calculate(targetAllocation);
            calcResults.put(targetAllocation, calcResult);
        }

        var table = new AssetAllocationTable();
        for (var entry : calcResults.entrySet()) {
            var target = entry.getKey();
            var calcResult = entry.getValue();

            var numeratorStr = target.getNumeratorFilterId();
            var denominatorFilterIdOpt = target.getDenominatorFilterIdOpt();
            var denominatorStr = denominatorFilterIdOpt.orElse(TOTAL_PORTFOLIO_STR);

            var colorWrapper = new DeviationStatusColorWrapper(calcResult.getDeviationStatus());

            var row = ImmAssetAllocationTableRow.of(
                    colorWrapper.wrap(numeratorStr),
                    colorWrapper.wrap(denominatorStr),
                    colorWrapper.wrap(formatCurrencyValue(calcResult.getCurrentNumeratorValue())),
                    colorWrapper.wrap(formatCurrencyValue(calcResult.getCurrentDenominatorValue())),
                    colorWrapper.wrap(formatFractionAsPercent(calcResult.getCurrentFraction())),
                    colorWrapper.wrap(formatFractionAsPercent(calcResult.getTargetFraction())),
                    colorWrapper.wrap(formatCurrencyValue(calcResult.getCorrectionNeeded())),
                    colorWrapper.wrap(formatFractionAsPercent(calcResult.getDeviationFraction())),
                    colorWrapper.wrap(calcResult.getDeviationStatus().toString())
            );
            table.addRow(row);
        }
        System.out.println(table.render());
    }

    private static String formatCurrencyValue(BigDecimal input) {
        var decimalAsStr = input.setScale(BIG_DECIMAL_DISPLAY_SCALE, ROUNDING_MODE)
                .toString();
        return "$" + decimalAsStr;
    }

    private static String formatFractionAsPercent(BigDecimal input) {
        var scaledUp = input.multiply(BigDecimal.valueOf(100));
        var decimalAsStr = scaledUp
                .setScale(BIG_DECIMAL_DISPLAY_SCALE, ROUNDING_MODE)
                .toString();
        return decimalAsStr + "%";
    }
}

