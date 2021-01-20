package com.strangegrotto.wealthdraft.backend.assetallocationcalc.api.types;

import java.math.BigDecimal;

public interface AssetAllocationCalcResult {
    BigDecimal getCurrentNumeratorValue();

    BigDecimal getCurrentDenominatorValue();

    BigDecimal getCurrentFraction();

    BigDecimal getTargetFraction();

    BigDecimal getTargetNumeratorValue();

    BigDecimal getCorrectionNeeded();

    BigDecimal getDeviationFraction();

    AssetAllocationDeviationStatus getDeviationStatus();
}
