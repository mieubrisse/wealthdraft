package com.strangegrotto.wealthdraft.errors;

import java.util.Optional;

// TODO Rename to "ValOrErr"
/**
 * Class intended to replicate Go's excellent error-handling and multiple return types
 * This class can have an optional value and/or an optional error
 */
public class ValOrGerr<T> {
    private final Optional<T> value;
    private final Optional<Gerr> error;

    private ValOrGerr(Optional<T> value, Optional<Gerr> error) {
        this.value = value;
        this.error = error;
    }

    public boolean hasValue() {
        return this.value.isPresent();
    }

    public T getVal() {
        return this.value.get();
    }

    public boolean hasGerr() {
        return this.error.isPresent();
    }

    public Gerr getGerr() {
        return this.error.get();
    }

    public static <T> ValOrGerr val(T value) {
        return new ValOrGerr(
                Optional.of(value),
                Optional.empty()
        );
    }

    /**
     * Creates a new {@link ValOrGerr} with the given message and optional formatting arguments
     */
    public static ValOrGerr newGerr(String message, Object... args) {
        // This is a nice convenience so we don't have to call `ValueOrGError.ofError(GError.newError(.....` and can
        //  do directly to `ValueOrGError.ofNewErr(...` BUT it means that we have to grab the stacktrace element and
        //  call the package-private GError.buildError method
        StackTraceElement stackTraceElem = Thread.currentThread().getStackTrace()[2];
        return new ValOrGerr(
                Optional.empty(),
                Optional.of(Gerr.build(
                        Optional.empty(),
                        stackTraceElem,
                        message,
                        args
                ))
        );
    }

    /**
     * Propagates an existing {@link Gerr}, wrapping it with a new message and args
     */
    public static ValOrGerr propGerr(Gerr err, String message, Object... args) {
        // This is a nice convenience so we don't have to call `ValueOrGError.ofError(GError.propagateError(.....` and can
        //  do directly to `ValueOrGError.ofPropagatedError(...` BUT it means that we have to grab the stacktrace element and
        //  call the package-private GError.buildError method
        StackTraceElement stackTraceElem = Thread.currentThread().getStackTrace()[2];
        return new ValOrGerr(
                Optional.empty(),
                Optional.of(Gerr.build(
                        Optional.of(err),
                        stackTraceElem,
                        message,
                        args
                ))
        );
    }

    // Add a "propagate if present" static function
}
