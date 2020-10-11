package com.strangegrotto.wealthdraft.validator;

import org.slf4j.helpers.MessageFormatter;

public class ValidationWarning {
    private final String formatString;
    private final Object[] args;

    private ValidationWarning(String formatString, Object... args) {
        this.formatString = formatString;
        this.args = args;
    }

    public static ValidationWarning of(String formatString, Object... args) {
        return new ValidationWarning(formatString, args);
    }

    public String getMessage() {
        return MessageFormatter.arrayFormat(this.formatString, this.args).getMessage();
    }
}
