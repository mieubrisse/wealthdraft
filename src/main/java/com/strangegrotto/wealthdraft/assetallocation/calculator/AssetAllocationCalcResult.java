package com.strangegrotto.wealthdraft.assetallocation.calculator;

import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import org.immutables.value.Value;

import java.math.BigDecimal;

@WealthdraftImmutableStyle
@Value.Immutable
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
