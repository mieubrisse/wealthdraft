package com.strangegrotto.wealthdraft.networth.projections;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = AssetChangeDeserializer.class)
public class AssetChange {
    private final long value;
    private final AssetChangeValueOperation operation;

    public AssetChange(long value, AssetChangeValueOperation operation) {
        this.value = value;
        this.operation = operation;
    }

    public long apply(long oldValue) {
        return this.operation.apply(oldValue, this.value);
    }
}
