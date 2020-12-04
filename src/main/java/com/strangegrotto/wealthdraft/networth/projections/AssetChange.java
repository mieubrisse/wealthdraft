package com.strangegrotto.wealthdraft.networth.projections;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.strangegrotto.wealthdraft.errors.ValOrGerr;

@JsonDeserialize(using = AssetChangeDeserializer.class)
public class AssetChange {
    private final long value;
    private final AssetChangeValueOperation operation;

    public AssetChange(long value, AssetChangeValueOperation operation) {
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
