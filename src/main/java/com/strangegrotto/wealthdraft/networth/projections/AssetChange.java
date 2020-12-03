package com.strangegrotto.wealthdraft.networth.projections;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.strangegrotto.wealthdraft.errors.ValueOrGError;

@JsonDeserialize(using = AssetChangeDeserializer.class)
public class AssetChange {
    private final long value;
    private final AssetChangeValueOperation operation;

    public AssetChange(long value, AssetChangeValueOperation operation) {
        this.value = value;
        this.operation = operation;
    }

    public ValueOrGError<Long> apply(long oldValue) {
        ValueOrGError<Long> result = this.operation.apply(oldValue, this.value);
        if (result.hasError()) {
            return ValueOrGError.ofPropagatedErr(
                    result.getError(),
                    "An error occurred applying operation '{}' to asset",
                    this.operation
            );
        }
        return result;
    }
}
