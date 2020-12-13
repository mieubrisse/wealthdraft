package com.strangegrotto.wealthdraft.networth.projections;

import com.strangegrotto.wealthdraft.errors.ValOrGerr;

import java.util.function.BiFunction;

// TODO this only supports longs right now!!
enum AssetParameterChangeValueOperation {
    // !!!!!!!!!!!!!!!!!!!!!!!!!! IMPORTANT !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    // If you add more change types here, make sure to add tests for them!
    // !!!!!!!!!!!!!!!!!!!!!!!!!! IMPORTANT !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    ADD((oldValue, newValue) -> ValOrGerr.val(oldValue + newValue)),
    SUBTRACT((oldValue, newValue) -> {
        if (oldValue < newValue) {
            return ValOrGerr.newGerr(
                    "Cannot apply asset change operation; current asset value '{}' is less than the value to be subtracted '{}'",
                    oldValue,
                    newValue);
        }
        return ValOrGerr.val(oldValue - newValue);
    }),
    SET((oldValue, newValue) -> ValOrGerr.val(newValue));

    private final BiFunction<Long, Long, ValOrGerr<Long>> mutator;

    AssetParameterChangeValueOperation(BiFunction<Long, Long, ValOrGerr<Long>> mutator) {
        this.mutator = mutator;
    }

    public ValOrGerr<Long> apply(long oldValue, long newValue) {
        return this.mutator.apply(oldValue, newValue);
    }
}

