package com.strangegrotto.wealthdraft.networth.projections;

import org.junit.Assert;
import org.junit.Test;

public class AssetParameterChangeValueOperationTest {
    @Test
    public void testAdd() {
        var oldValue = 10L;
        var toAdd = 5L;
        var expectedResult = oldValue + toAdd;
        var resultOrErr = AssetParameterChangeValueOperation.ADD.apply(oldValue, toAdd);
        Assert.assertFalse("Expected no error, but an error was returned", resultOrErr.hasGerr());
        Assert.assertEquals(expectedResult, resultOrErr.getVal().longValue());
    }

    @Test
    public void testSubtract() {
        var oldValue = 10L;
        var toSubtract = 5L;
        var expectedResult = oldValue - toSubtract;
        var resultOrErr = AssetParameterChangeValueOperation.SUBTRACT.apply(oldValue, toSubtract);
        Assert.assertFalse("Expected no error, but an error was returned", resultOrErr.hasGerr());
        Assert.assertEquals(expectedResult, resultOrErr.getVal().longValue());
    }

    @Test
    public void testOversubtractionError() {
        var oldValue = 5L;
        var toSubtract = 10L;
        var expectedResult = oldValue - toSubtract;
        var resultOrErr = AssetParameterChangeValueOperation.SUBTRACT.apply(oldValue, toSubtract);
        Assert.assertTrue("Expected an error on oversubtraction", resultOrErr.hasGerr());
    }

    @Test
    public void testSet() {
        var oldValue = 5L;
        var toSet = 100L;
        var resultOrErr = AssetParameterChangeValueOperation.SUBTRACT.apply(oldValue, toSet);
        Assert.assertFalse("Expected no error, but an error was returned", resultOrErr.hasGerr());
        Assert.assertEquals(toSet, resultOrErr.getVal().longValue());
    }
}
