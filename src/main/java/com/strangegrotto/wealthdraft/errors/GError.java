package com.strangegrotto.wealthdraft.errors;

import org.slf4j.helpers.MessageFormatter;

import java.util.Optional;
import java.util.StringJoiner;

/**
 * Class intended to mirror Palantir's 'stacktrace' library in Go, which has a very elegant interface for capturing
 *  the source of errors.
 */
public class GError {
    String message;
    StackTraceElement stackTraceElem;
    Optional<GError> cause;

    private GError(String message, StackTraceElement stackTraceElem, Optional<GError> cause) {
        this.message = message;
        this.stackTraceElem = stackTraceElem;
        this.cause = cause;
    }

    static GError buildError(Optional<GError> cause, StackTraceElement stackTraceElem, String message, Object... args) {
        String formattedMsg = MessageFormatter.arrayFormat(message, args).getMessage();
        return new GError(formattedMsg, stackTraceElem, cause);
    }

    public static GError newError(String message, Object... args) {
        StackTraceElement stackTraceElem = Thread.currentThread().getStackTrace()[2];
        return buildError(Optional.empty(), stackTraceElem, message, args);
    }

    public static GError propagate(GError err, String message, Object... args) {
        StackTraceElement stackTraceElem = Thread.currentThread().getStackTrace()[2];
        return buildError(Optional.of(err), stackTraceElem, message, args);
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add(this.message);
        joiner.add(" -- " + stackTraceElem.toString());
        if (this.cause.isPresent()) {
            String causeStr = this.cause.get().toString();
            joiner.add(causeStr);
        }
        return joiner.toString();
    }
}
