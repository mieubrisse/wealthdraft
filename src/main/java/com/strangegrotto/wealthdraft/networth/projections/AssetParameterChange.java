package com.strangegrotto.wealthdraft.networth.projections;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.annotations.VisibleForTesting;
import com.strangegrotto.wealthdraft.errors.ValOrGerr;
import org.immutables.value.Value;

// TODO Upgrade this to support BigDecimal
@Value.Immutable
@JsonDeserialize(using = AssetParameterChangeDeserializer.class)
public interface AssetParameterChange {
    long getValue();

    AssetParameterChangeValueOperation getOperation();

    @Value.Derived
    default ValOrGerr<Long> apply(long oldValue) {
        ValOrGerr<Long> result = getOperation().apply(oldValue, getValue());
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
