package com.strangegrotto.wealthdraft.assetallocation.renderer;

import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import org.immutables.value.Value;

@WealthdraftImmutableStyle
@Value.Immutable
abstract class AssetAllocationTableRow {
    abstract String getNumeratorStr();
    abstract String getDenominatorStr();
    abstract String getCurrentNumDenomPct();
    abstract String getTargetNumDenomPct();
    abstract String getCorrectionNeeded();
    abstract String getDeviationPct();
    abstract String getDeviationStatus();
}
