package com.strangegrotto.wealthdraft.validator;

/**
 * Class of static validation methods
 */
public class DeserializationValidator {
    private DeserializationValidator(){}

    public static void checkIsRatio(String fieldName, Double value) {
        if (value < 0 || value > 1) {
            throw new IllegalArgumentException(
                    String.format(
                            "Field '%s' must be in range [0.0, 1.0] but was %s",
                            fieldName,
                            value
                    )
            );
        }
    }
}
