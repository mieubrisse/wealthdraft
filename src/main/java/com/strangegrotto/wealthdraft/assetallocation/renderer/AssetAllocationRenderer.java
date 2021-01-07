package com.strangegrotto.wealthdraft.assetallocation.renderer;

import com.jakewharton.picnic.CellStyle;
import com.jakewharton.picnic.Table;
import com.jakewharton.picnic.TableSection;
import com.jakewharton.picnic.TextAlignment;
import com.strangegrotto.wealthdraft.Display;
import com.strangegrotto.wealthdraft.assetallocation.calculator.AssetAllocationCalcResult;
import com.strangegrotto.wealthdraft.assetallocation.datamodel.TargetAssetAllocation;
import com.strangegrotto.wealthdraft.assetallocation.datamodel.TargetAssetAllocations;
import com.strangegrotto.wealthdraft.assetallocation.datamodel.filters.AssetFilter;
import com.strangegrotto.wealthdraft.assets.definition.Asset;
import com.strangegrotto.wealthdraft.assets.temporal.AssetSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// TODO Convert this, and all the other calculator-renderers (e.g. NetWorthRenderer) to return POJOs so that
//  we split up the calculation & rendering step (and can write good unit tests)
public class AssetAllocationRenderer {
    // TODO Replace both of these with some sort of mathematical constant supplier, so config is centralized
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_EVEN;
    private static final int BIG_DECIMAL_DISPLAY_SCALE = 2;  // TODO replace this with something on Display

    // The string displayed when the denominator isn't specified and we're using the whole portfolio
    private static final String TOTAL_PORTFOLIO_STR = "Total Portfolio";

    private final Display display;

    public AssetAllocationRenderer(Display display) {
        this.display = display;
    }

    public void render(
            LinkedHashMap<TargetAssetAllocation, AssetAllocationCalcResult> calcResults) {
        display.printEmptyLine();
        display.printBannerHeader("Asset Allocations");

        var table = new AssetAllocationTable();
        for (var entry : calcResults.entrySet()) {
            var target = entry.getKey();
            var calcResult = entry.getValue();

            var numeratorStr = target.getNumeratorFilter();
            var denominatorFilterNameOpt = target.getDenominatorFilterOpt();
            var denominatorStr = denominatorFilterNameOpt.orElse(TOTAL_PORTFOLIO_STR);

            var row = ImmAssetAllocationTableRow.of(
                    numeratorStr,
                    denominatorStr,
                    formatFractionAsPercent(calcResult.getCurrentFraction()),
                    formatFractionAsPercent(calcResult.getDesiredFraction()),
                    formatCurrencyValue(calcResult.getCorrectionNeeded()),
                    formatFractionAsPercent(calcResult.getDeviationFraction()),
                    calcResult.getDeviationStatus().toString()
            );
            table.addRow(row);
        }
        System.out.println(table.render());
    }

    private static String formatCurrencyValue(BigDecimal input) {
        return input.setScale(BIG_DECIMAL_DISPLAY_SCALE, ROUNDING_MODE)
                .toString();
    }

    private static String formatFractionAsPercent(BigDecimal input) {
        var scaledUp = input.multiply(BigDecimal.valueOf(100));
        return scaledUp
                .setScale(BIG_DECIMAL_DISPLAY_SCALE, ROUNDING_MODE)
                .toString();
    }
}

