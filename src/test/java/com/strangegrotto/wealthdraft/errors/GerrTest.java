package com.strangegrotto.wealthdraft.errors;

import org.junit.Test;

public class GerrTest {
    @Test
    public void testToString() {
        Gerr test = Gerr.newGerr("This is a test");
        Gerr propagated = Gerr.propGerr(test, "This is the outer error");
        System.out.println(propagated.toString());
    }
}
