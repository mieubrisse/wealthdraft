package com.strangegrotto.wealthdraft.networth.projections;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.strangegrotto.wealthdraft.errors.ValOrGerr;

// TODO Upgrade this to support BigDecimal
@JsonDeserialize(using = AssetParameterChangeDeserializer.class)
public class AssetParameterChange {
    private final long value;
    private final AssetParameterChangeValueOperation operation;

    public AssetParameterChange(long value, AssetParameterChangeValueOperation operation) {
        this.value = value;
        this.operation = operation;
    }

    public ValOrGerr<Long> apply(long oldValue) {
        ValOrGerr<Long> result = this.operation.apply(oldValue, this.value);
        if (result.hasGerr()) {
            return ValOrGerr.propGerr(
                    result.getGerr(),
                    "An error occurred applying operation '{}' to asset",
                    this.operation
            );
        }
        return result;
    }
}
