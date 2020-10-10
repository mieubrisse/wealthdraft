package com.strangegrotto.wealthdraft.errors;

import jdk.jfr.StackTrace;

import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

/**
 * Class intended to mirror Palantir's 'stacktrace' library in Go, which has a very elegant interface for capturing
 *  the source of errors.
 */
public class Error {
    String message;
    StackTraceElement stackTraceElem;
    Optional<Error> cause;

    private Error(String message, StackTraceElement stackTraceElem, Optional<Error> cause) {
        this.message = message;
        this.stackTraceElem = stackTraceElem;
        this.cause = cause;
    }

    private static Error buildError(Optional<Error> cause, StackTraceElement stackTraceElem, String message, Object... args) {
        String formattedMsg = String.format(message, args);
        return new Error(formattedMsg, stackTraceElem, cause);
    }

    public static Error newError(String message, Object... args) {
        StackTraceElement stackTraceElem = Thread.currentThread().getStackTrace()[2];
        return buildError(Optional.empty(), stackTraceElem, message, args);
    }

    public static Error propagate(Error err, String message, Object... args) {
        StackTraceElement stackTraceElem = Thread.currentThread().getStackTrace()[2];
        return buildError(Optional.of(err), stackTraceElem, message, args);
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner("\n");
        if (this.cause.isPresent()) {
            String causeStr = this.cause.get().toString();
            joiner.add(causeStr);
        }
        joiner.add(this.message);
        joiner.add(" -- " + stackTraceElem.toString());
        return joiner.toString();
    }
}
