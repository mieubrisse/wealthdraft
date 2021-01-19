package com.strangegrotto.wealthdraft.assetallocation.api.types;

import java.math.BigDecimal;
import java.util.Optional;

public interface TargetAssetAllocation {
    String getNumeratorFilterId();

    // If not present, the denominator is assumed to be the entire portfolio
    Optional<String> getDenominatorFilterId();

    BigDecimal getFraction();
}
