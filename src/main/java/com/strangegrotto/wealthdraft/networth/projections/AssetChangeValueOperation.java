package com.strangegrotto.wealthdraft.networth.projections;

import com.strangegrotto.wealthdraft.errors.ValueOrGError;

import java.util.function.BiFunction;

enum AssetChangeValueOperation {
    ADD((oldValue, newValue) -> ValueOrGError.ofValue(oldValue + newValue)),
    SUBTRACT((oldValue, newValue) -> {
        if (oldValue < newValue) {
            return ValueOrGError.ofNewErr(
                    "Cannot apply asset change operation; current asset value '{}' is less than the value to be subtracted '{}'",
                    oldValue,
                    newValue);
        }
        return ValueOrGError.ofValue(oldValue - newValue);
    }),
    SET((oldValue, newValue) -> ValueOrGError.ofValue(newValue));

    private final BiFunction<Long, Long, ValueOrGError<Long>> mutator;

    AssetChangeValueOperation(BiFunction<Long, Long, ValueOrGError<Long>> mutator) {
        this.mutator = mutator;
    }

    public ValueOrGError<Long> apply(long oldValue, long newValue) {
        return this.mutator.apply(oldValue, newValue);
    }
}

