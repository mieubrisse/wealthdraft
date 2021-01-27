package com.strangegrotto.wealthdraft.backend.projections.api.types;

import com.strangegrotto.wealthdraft.errors.ValOrGerr;

import java.math.BigDecimal;

public interface AssetParameterChange {
    ValOrGerr<BigDecimal> apply(BigDecimal oldValue);
}
