package com.strangegrotto.wealthdraft.networth.projections;

import com.strangegrotto.wealthdraft.errors.ValOrGerr;

import java.math.BigDecimal;
import java.util.function.BiFunction;

// TODO this only supports BigDecimals right now!!
public enum AssetParameterChangeValueOperation {
    // !!!!!!!!!!!!!!!!!!!!!!!!!! IMPORTANT !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    // If you add more change types here, make sure to add tests for them!
    // !!!!!!!!!!!!!!!!!!!!!!!!!! IMPORTANT !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    ADD((oldValue, newValue) -> ValOrGerr.val(oldValue.add(newValue))),
    SUBTRACT((oldValue, newValue) -> {
        if (oldValue.compareTo(newValue) < 0) {
            return ValOrGerr.newGerr(
                    "Cannot apply asset change operation; current asset value '{}' is less than the value to be subtracted '{}'",
                    oldValue,
                    newValue);
        }
        return ValOrGerr.val(oldValue.subtract(newValue));
    }),
    SET((oldValue, newValue) -> ValOrGerr.val(newValue));

    private final BiFunction<BigDecimal, BigDecimal, ValOrGerr<BigDecimal>> mutator;

    AssetParameterChangeValueOperation(BiFunction<BigDecimal, BigDecimal, ValOrGerr<BigDecimal>> mutator) {
        this.mutator = mutator;
    }

    public ValOrGerr<BigDecimal> apply(BigDecimal oldValue, BigDecimal newValue) {
        return this.mutator.apply(oldValue, newValue);
    }
}

