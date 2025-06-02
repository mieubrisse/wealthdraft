package com.strangegrotto.wealthdraft.assetallocation.renderer;

import org.immutables.value.Value;

import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;

@WealthdraftImmutableStyle
@Value.Immutable
abstract class AssetAllocationTableRow {
    abstract String getNumeratorStr();
    abstract String getDenominatorStr();
    abstract String getNumeratorValue();
    abstract String getDenominatorValue();
    abstract String getNumDenomPct();
    abstract String getTargetNumDenomPct();
    abstract String getTargetAmount();
    abstract String getCorrectionNeeded();
    abstract String getDeviationPct();
    abstract String getDeviationStatus();
}
