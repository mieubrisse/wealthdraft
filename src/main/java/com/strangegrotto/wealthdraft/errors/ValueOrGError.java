package com.strangegrotto.wealthdraft.errors;

import java.util.Optional;

/**
 * Class intended to replicate Go's excellent error-handling and multiple return types
 * This class can have an optional value and/or an optional error
 */
public class ValueOrGError<T> {
    private final Optional<T> value;
    private final Optional<GError> error;

    private ValueOrGError(Optional<T> value, Optional<GError> error) {
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

    public static <T> ValueOrGError ofValue(T value) {
        return new ValueOrGError(
                Optional.of(value),
                Optional.empty()
        );
    }

    // This is a nice convenience so we don't have to call `ValueOrGError.ofError(GError.newError(.....` and can
    //  do directly to `ValueOrGError.ofNewErr(...` BUT it means that we have to grab the stacktrace element and
    //  call the package-private GError.buildError method
    public static ValueOrGError ofNewErr(String message, Object... args) {
        StackTraceElement stackTraceElem = Thread.currentThread().getStackTrace()[2];
        return new ValueOrGError(
                Optional.empty(),
                Optional.of(GError.buildError(
                        Optional.empty(),
                        stackTraceElem,
                        message,
                        args
                ))
        );
    }

    // This is a nice convenience so we don't have to call `ValueOrGError.ofError(GError.propagateError(.....` and can
    //  do directly to `ValueOrGError.ofPropagatedError(...` BUT it means that we have to grab the stacktrace element and
    //  call the package-private GError.buildError method
    public static ValueOrGError ofPropagatedErr(GError err, String message, Object... args) {
        StackTraceElement stackTraceElem = Thread.currentThread().getStackTrace()[2];
        return new ValueOrGError(
                Optional.empty(),
                Optional.of(GError.buildError(
                        Optional.of(err),
                        stackTraceElem,
                        message,
                        args
                ))
        );
    }
}
