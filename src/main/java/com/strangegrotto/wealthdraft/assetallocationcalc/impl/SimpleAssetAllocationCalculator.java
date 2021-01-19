package com.strangegrotto.wealthdraft.assetallocationcalc.impl;

import com.google.common.annotations.VisibleForTesting;
import com.strangegrotto.wealthdraft.assetallocation.api.types.TargetAssetAllocation;
import com.strangegrotto.wealthdraft.assetallocationcalc.api.AssetAllocationCalculator;
import com.strangegrotto.wealthdraft.assetallocationcalc.api.types.AssetAllocationCalcResult;
import com.strangegrotto.wealthdraft.assetallocationcalc.api.types.AssetAllocationDeviationStatus;
import com.strangegrotto.wealthdraft.assetallocation.impl.SerTargetAssetAllocation;
import com.strangegrotto.wealthdraft.assetallocation.impl.SerTargetAssetAllocations;
import com.strangegrotto.wealthdraft.assethistory.api.AssetHistoryStore;
import com.strangegrotto.wealthdraft.assets.api.AssetsStore;
import com.strangegrotto.wealthdraft.filters.api.FiltersStore;
import com.strangegrotto.wealthdraft.filters.impl.SerAssetFilter;
import com.strangegrotto.wealthdraft.assets.impl.SerAsset;
import com.strangegrotto.wealthdraft.assethistory.api.types.AssetSnapshot;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class SimpleAssetAllocationCalculator implements AssetAllocationCalculator {
    // The rounding parameters to use so that non-terminating divison doesn't throw an exception
    @VisibleForTesting static final int DIVISION_SCALE = 6;
    @VisibleForTesting static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_EVEN;

    private final BigDecimal deviationFractionWarn;
    private final BigDecimal deviationFractionErr;
    private final FiltersStore filtersStore;
    private final AssetsStore assetsStore;
    private final AssetHistoryStore assetHistoryStore;

    public SimpleAssetAllocationCalculator(BigDecimal deviationFractionWarn, BigDecimal deviationFractionErr, FiltersStore filtersStore, AssetsStore assetsStore, AssetHistoryStore assetHistoryStore) {
        this.deviationFractionWarn = deviationFractionWarn;
        this.deviationFractionErr = deviationFractionErr;
        this.filtersStore = filtersStore;
        this.assetsStore = assetsStore;
        this.assetHistoryStore = assetHistoryStore;
    }

    @Override
    public AssetAllocationCalcResult calculate(TargetAssetAllocation target) {




        var totalPortfolioValue = latestAssetSnapshots.values().stream()
                .map(AssetSnapshot::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        var filters = targetAssetAllocations.getFilters();
        var targets = targetAssetAllocations.getTargets();

        var results = new LinkedHashMap<SerTargetAssetAllocation, SerAssetAllocationCalcResult>();
        for (var target : targets) {
            var numeratorFilterName = target.getNumeratorFilter();
            var numeratorFilter = filters.get(numeratorFilterName);
            var numeratorValue = getValueOfAssetsMatchingFilter(assets, latestAssetSnapshots, filters, numeratorFilter);

            var denominatorFilterNameOpt = target.getDenominatorFilterOpt();
            BigDecimal denominatorValue;
            String denominatorStrRepr;
            if (denominatorFilterNameOpt.isPresent()) {
                var denominatorFilterName = denominatorFilterNameOpt.get();
                var denominatorFilter = filters.get(denominatorFilterName);
                denominatorValue = getValueOfAssetsMatchingFilter(assets, latestAssetSnapshots, filters, denominatorFilter);
                denominatorStrRepr = denominatorFilterName;
            } else {
                denominatorValue = totalPortfolioValue;
                denominatorStrRepr = "Total Portfolio";
            }

            var calcResult = calcSingleAssetAllocation(
                    numeratorValue,
                    denominatorValue,
                    target.getFraction(),
                    this.deviationFractionWarn,
                    this.deviationFractionErr
            );
            results.put(target, calcResult);
        }
        return results;
    }

    @VisibleForTesting
    static SerAssetAllocationCalcResult calcSingleAssetAllocation(
            BigDecimal numeratorValue,
            BigDecimal denominatorValue,
            BigDecimal targetFraction,
            BigDecimal deviationFractionWarn,
            BigDecimal deviationFractionErr
    ) {
        var currentFraction = numeratorValue.divide(
                denominatorValue,
                DIVISION_SCALE,
                ROUNDING_MODE
        );
        var targetNumeratorValue = targetFraction.multiply(denominatorValue);
        var correctionNeeded = targetNumeratorValue.subtract(numeratorValue);
        var deviationFraction = correctionNeeded.abs().divide(
                targetNumeratorValue,
                DIVISION_SCALE,
                ROUNDING_MODE
        );
        AssetAllocationDeviationStatus deviationStatus;
        if (deviationFraction.compareTo(deviationFractionErr) >= 0) {
            deviationStatus = AssetAllocationDeviationStatus.ERROR;
        } else if (deviationFraction.compareTo(deviationFractionWarn) >= 0) {
            deviationStatus = AssetAllocationDeviationStatus.WARN;
        } else {
            deviationStatus = AssetAllocationDeviationStatus.OK;
        }

        return ImmAssetAllocationCalcResult.of(
                numeratorValue,
                denominatorValue,
                currentFraction,
                targetFraction,
                targetNumeratorValue,
                correctionNeeded,
                deviationFraction,
                deviationStatus
        );
    }

    private static BigDecimal getValueOfAssetsMatchingFilter(
            Map<String, SerAsset> assets,
            Map<String, AssetSnapshot<?>> latestAssetSnapshots,
            Map<String, SerAssetFilter> filters,
            SerAssetFilter filter) {
        var matchingAssetIds = filter.apply(filters, assets);
        return latestAssetSnapshots.entrySet().stream()
                .filter(entry -> matchingAssetIds.containsKey(entry.getKey()))
                .map(Map.Entry::getValue)
                .map(AssetSnapshot::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
