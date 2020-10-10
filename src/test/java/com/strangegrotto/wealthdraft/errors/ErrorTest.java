package com.strangegrotto.wealthdraft.errors;

import org.junit.Test;

public class ErrorTest {
    @Test
    public void testToString() {
        Error test = Error.newError("This is a test");
        Error propagated = Error.propagate(test, "This is the outer error");
        System.out.println(propagated.toString());
    }
}
