package com.strangegrotto.wealthdraft.assets.definition;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.Test;

public class CustomTagDefinitionTest {
    @SuppressFBWarnings(
            value = "RV_RETURN_VALUE_IGNORED",
            justification = "Testing Immutables check method; return value not used"
    )
    @Test(expected = IllegalStateException.class)
    public void testErrorOnUnrecognizedDefaultValue() {
        //noinspection ResultOfMethodCallIgnored
        ImmCustomTagDefinition.builder()
                .addAllowedValues("value1", "value2")
                .defaultValue("notInList")
                .build();
    }

    @SuppressFBWarnings(
            value = "RV_RETURN_VALUE_IGNORED",
            justification = "Testing Immutables check method; return value not used"
    )
    @Test
    public void testNoErrorOnNoExplicitAllowedValues() {
        // Shouldn't throw an error because no explicit allowed values have been set (allowing all values)
        //noinspection ResultOfMethodCallIgnored
        ImmCustomTagDefinition.builder()
                .defaultValue("defaultValue")
                .build();
    }
}
