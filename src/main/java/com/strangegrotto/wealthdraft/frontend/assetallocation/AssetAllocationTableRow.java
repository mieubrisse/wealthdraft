package com.strangegrotto.wealthdraft.frontend.assetallocation;

import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import org.immutables.value.Value;

@WealthdraftImmutableStyle
@Value.Immutable
abstract class AssetAllocationTableRow {
    abstract String getNumeratorStr();
    abstract String getDenominatorStr();
    abstract String getNumeratorValue();
    abstract String getDenominatorValue();
    abstract String getCurrentNumDenomPct();
    abstract String getTargetNumDenomPct();
    abstract String getCorrectionNeeded();
    abstract String getDeviationPct();
    abstract String getDeviationStatus();
}
