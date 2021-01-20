package com.strangegrotto.wealthdraft.backend.tagstores.custom.impl;

import com.strangegrotto.wealthdraft.backend.assets.impl.ImmCustomTagDefinition;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.Test;

public class SerCustomTagDefinitionTest {
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
