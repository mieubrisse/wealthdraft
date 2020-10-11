package com.strangegrotto.wealthdraft.errors;

import org.junit.Test;

public class GErrorTest {
    @Test
    public void testToString() {
        GError test = GError.newError("This is a test");
        GError propagated = GError.propagate(test, "This is the outer error");
        System.out.println(propagated.toString());
    }
}
