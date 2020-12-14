package com.strangegrotto.wealthdraft.networth.projections;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

public class AssetParameterChangeValueOperationTest {
    @Test
    public void testAdd() {
        var oldValue = new BigDecimal(10);
        var toAdd = new BigDecimal(5);
        var expectedResult = oldValue.add(toAdd);
        var resultOrErr = AssetParameterChangeValueOperation.ADD.apply(oldValue, toAdd);
        Assert.assertFalse("Expected no error, but an error was returned", resultOrErr.hasGerr());
        Assert.assertEquals(expectedResult, resultOrErr.getVal());
    }

    @Test
    public void testSubtract() {
        var oldValue = new BigDecimal(10);
        var toSubtract = new BigDecimal(5);
        var expectedResult = oldValue.subtract(toSubtract);
        var resultOrErr = AssetParameterChangeValueOperation.SUBTRACT.apply(oldValue, toSubtract);
        Assert.assertFalse("Expected no error, but an error was returned", resultOrErr.hasGerr());
        Assert.assertEquals(expectedResult, resultOrErr.getVal());
    }

    @Test
    public void testOversubtractionError() {
        var oldValue = new BigDecimal(5);
        var toSubtract = new BigDecimal(10);
        var resultOrErr = AssetParameterChangeValueOperation.SUBTRACT.apply(oldValue, toSubtract);
        Assert.assertTrue("Expected an error on oversubtraction", resultOrErr.hasGerr());
    }

    @Test
    public void testSet() {
        var oldValue = new BigDecimal(5);
        var toSet = new BigDecimal(100);
        var resultOrErr = AssetParameterChangeValueOperation.SET.apply(oldValue, toSet);
        Assert.assertFalse("Expected no error, but an error was returned", resultOrErr.hasGerr());
        Assert.assertEquals(toSet, resultOrErr.getVal());
    }
}
