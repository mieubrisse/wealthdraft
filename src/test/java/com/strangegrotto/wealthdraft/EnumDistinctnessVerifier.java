package com.strangegrotto.wealthdraft;

import org.junit.Assert;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;

/**
 * Verifies the distinctness of a property of an enum
 */
public class EnumDistinctnessVerifier {
    private EnumDistinctnessVerifier() {}

    public static <T extends Enum> void verifyDistinct(T[] values, Function<T, ?> valueExtractor) {
        var expectedCount = values.length;
        var actualCount = Arrays.stream(values)
                .map(valueExtractor)
                .filter(Objects::nonNull)
                .distinct()
                .count();
        Assert.assertEquals(expectedCount, actualCount);
    }
}
