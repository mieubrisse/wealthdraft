package com.strangegrotto.wealthdraft.errors;

import java.util.Optional;

/**
 * Class intended to replicate Go's excellent error-handling and multiple return types
 * This class can have an optional value and/or an optional error
 */
public class ValueOrError<T> {
    private final Optional<T> value;
    private final Optional<GError> error;

    private ValueOrError(Optional<T> value, Optional<GError> error) {
        this.value = value;
        this.error = error;
    }

    public boolean hasValue() {
        return this.value.isPresent();
    }

    public T getValue() {
        return this.value.get();
    }

    public boolean hasError() {
        return this.error.isPresent();
    }

    public GError getError() {
        return this.error.get();
    }

    public static <T> ValueOrError ofValue(T value) {
        return new ValueOrError(
                Optional.of(value),
                Optional.empty()
        );
    }

    public static ValueOrError ofError(GError error) {
        return new ValueOrError(
                Optional.empty(),
                Optional.of(error)
        );
    }
}
