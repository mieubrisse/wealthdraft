package com.strangegrotto.wealthdraft.networth.projections;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.strangegrotto.wealthdraft.errors.ValOrGerr;
import org.immutables.value.Value;

import java.math.BigDecimal;

@Value.Immutable
public interface AssetParameterChange {
    BigDecimal getValue();

    AssetParameterChangeValueOperation getOperation();

    @Value.Derived
    default ValOrGerr<BigDecimal> apply(BigDecimal oldValue) {
        ValOrGerr<BigDecimal> result = getOperation().apply(oldValue, getValue());
        if (result.hasGerr()) {
            return ValOrGerr.propGerr(
                    result.getGerr(),
                    "An error occurred applying operation '{}' to asset",
                    getOperation()
            );
        }
        return result;
    }
}
