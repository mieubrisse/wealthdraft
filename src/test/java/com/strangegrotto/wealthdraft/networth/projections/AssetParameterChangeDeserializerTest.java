package com.strangegrotto.wealthdraft.networth.projections;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

public class AssetParameterChangeDeserializerTest {
    @Test
    public void testAddDeserialization() {
        var mapper = new ObjectMapper();
        var value = 15000L;
        AssetParameterChange change = null;
        try {
            change = mapper.readValue("+" + value, AssetParameterChange.class);
        } catch (JsonProcessingException e) {
            Assert.fail("No exception should have been thrown on parsing");
        }
        Assert.assertEquals(value, change.value);
        Assert.assertEquals(AssetParameterChangeValueOperation.ADD, change.operation);
    }

    @Test
    public void testSubtractDeserialization() {
        var mapper = new ObjectMapper();
        var value = 15000L;
        AssetParameterChange change = null;
        try {
            change = mapper.readValue("-" + value, AssetParameterChange.class);
        } catch (JsonProcessingException e) {
            Assert.fail("No exception should have been thrown on parsing");
        }
        Assert.assertEquals(value, change.value);
        Assert.assertEquals(AssetParameterChangeValueOperation.SUBTRACT, change.operation);
    }

    @Test
    public void testSetDeserialization() {
        var mapper = new ObjectMapper();
        var value = 15000L;
        AssetParameterChange change = null;
        try {
            change = mapper.readValue(Long.toString(value), AssetParameterChange.class);
        } catch (JsonProcessingException e) {
            Assert.fail("No exception should have been thrown on parsing");
        }
        Assert.assertEquals(value, change.value);
        Assert.assertEquals(AssetParameterChangeValueOperation.SET, change.operation);
    }
}
