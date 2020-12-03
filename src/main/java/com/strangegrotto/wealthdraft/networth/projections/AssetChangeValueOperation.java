package com.strangegrotto.wealthdraft.networth.projections;

import java.util.function.BiFunction;

enum AssetChangeValueOperation {
    ADD((oldValue, newValue) -> oldValue + newValue),
    SUBTRACT((oldValue, newValue) -> Math.max(0L, oldValue - newValue)),
    SET((oldValue, newValue) -> newValue);

    private final BiFunction<Long, Long, Long> mutator;

    AssetChangeValueOperation(BiFunction<Long, Long, Long> mutator) {
        this.mutator = mutator;
    }

    public long apply(long oldValue, long newValue) {
        return this.mutator.apply(oldValue, newValue);
    }
}

