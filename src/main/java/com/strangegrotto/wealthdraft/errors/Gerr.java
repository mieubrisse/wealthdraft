package com.strangegrotto.wealthdraft.errors;

import org.slf4j.helpers.MessageFormatter;

import java.util.Optional;
import java.util.StringJoiner;

/**
 * Class intended to mirror Palantir's 'stacktrace' library in Go, which has a very elegant interface for capturing
 *  the source of errors.
 *
 * Why "Gerr"? Golang + Error = Gerr
 */
public class Gerr {
    String message;
    StackTraceElement stackTraceElem;
    Optional<Gerr> cause;

    private Gerr(String message, StackTraceElement stackTraceElem, Optional<Gerr> cause) {
        this.message = message;
        this.stackTraceElem = stackTraceElem;
        this.cause = cause;
    }

    static Gerr build(Optional<Gerr> cause, StackTraceElement stackTraceElem, String message, Object... args) {
        String formattedMsg = MessageFormatter.arrayFormat(message, args).getMessage();
        return new Gerr(formattedMsg, stackTraceElem, cause);
    }

    public static Gerr newGerr(String message, Object... args) {
        StackTraceElement stackTraceElem = Thread.currentThread().getStackTrace()[2];
        return build(Optional.empty(), stackTraceElem, message, args);
    }

    public static Gerr propGerr(Gerr err, String message, Object... args) {
        StackTraceElement stackTraceElem = Thread.currentThread().getStackTrace()[2];
        return build(Optional.of(err), stackTraceElem, message, args);
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
