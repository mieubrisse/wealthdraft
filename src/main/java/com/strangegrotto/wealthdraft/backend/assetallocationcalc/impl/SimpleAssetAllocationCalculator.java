package com.strangegrotto.wealthdraft.backend.assetallocationcalc.impl;

import com.google.common.annotations.VisibleForTesting;
import com.strangegrotto.wealthdraft.backend.assetallocation.api.types.TargetAssetAllocation;
import com.strangegrotto.wealthdraft.backend.assetallocationcalc.api.AssetAllocationCalculator;
import com.strangegrotto.wealthdraft.backend.assetallocationcalc.api.types.AssetAllocationCalcResult;
import com.strangegrotto.wealthdraft.backend.assetallocationcalc.api.types.AssetAllocationDeviationStatus;
import com.strangegrotto.wealthdraft.backend.assethistory.api.AssetHistoryStore;
import com.strangegrotto.wealthdraft.backend.assethistory.api.types.AssetSnapshot;
import com.strangegrotto.wealthdraft.backend.assets.api.AssetsStore;
import com.strangegrotto.wealthdraft.backend.assets.api.types.Asset;
import com.strangegrotto.wealthdraft.backend.filters.api.FiltersStore;
import com.strangegrotto.wealthdraft.backend.filters.api.types.AssetFilter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

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
        var assets = this.assetsStore.getAssets();

        var assetHistory = this.assetHistoryStore.getHistory();
        var latestDate = assetHistory.lastKey();
        var latestAssetSnapshots = assetHistory.get(latestDate);

        var totalPortfolioValue = latestAssetSnapshots.values().stream()
                .map(AssetSnapshot::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        var filters = this.filtersStore.getFilters();

        var numeratorFilterId = target.getNumeratorFilterId();
        var numeratorFilter = filters.get(numeratorFilterId);
        var numeratorValue = getValueOfAssetsMatchingFilter(assets, latestAssetSnapshots, filters, numeratorFilter);

        var denominatorFilterIdOpt = target.getDenominatorFilterIdOpt();
        BigDecimal denominatorValue;
        String denominatorStrRepr;
        if (denominatorFilterIdOpt.isPresent()) {
            var denominatorFilterName = denominatorFilterIdOpt.get();
            var denominatorFilter = filters.get(denominatorFilterName);
            denominatorValue = getValueOfAssetsMatchingFilter(assets, latestAssetSnapshots, filters, denominatorFilter);
            denominatorStrRepr = denominatorFilterName;
        } else {
            denominatorValue = totalPortfolioValue;
            denominatorStrRepr = "Total Portfolio";
        }

        return calcSingleAssetAllocation(
                numeratorValue,
                denominatorValue,
                target.getFraction(),
                this.deviationFractionWarn,
                this.deviationFractionErr
        );
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

        return ImmSerAssetAllocationCalcResult.of(
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
            Map<String, Asset> assets,
            Map<String, AssetSnapshot<?>> latestAssetSnapshots,
            Map<String, AssetFilter> filters,
            AssetFilter filter) {
        var matchingAssetIds = filter.apply(filters, assets);
        return latestAssetSnapshots.entrySet().stream()
                .filter(entry -> matchingAssetIds.containsKey(entry.getKey()))
                .map(Map.Entry::getValue)
                .map(AssetSnapshot::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
