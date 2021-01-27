package com.strangegrotto.wealthdraft.backend.projections.impl.temporal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.strangegrotto.wealthdraft.Main;
import com.strangegrotto.wealthdraft.backend.projections.api.types.AssetParameterChange;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;

public class SerAssetParameterChangeDeserializerTest {
    @Test
    public void testAddDeserialization() throws IOException {
        var mapper = getMapperWithDeserializer();
        var value = 15000L;
        SerAssetParameterChange change = null;
        change = mapper.readValue("+" + value, SerAssetParameterChange.class);
        Assert.assertEquals(new BigDecimal(value), change.getValue());
        Assert.assertEquals(AssetParameterChangeValueOperation.ADD, change.getOperation());
    }

    @Test
    public void testSubtractDeserialization() throws IOException {
        var mapper = getMapperWithDeserializer();
        var value = 15000L;
        SerAssetParameterChange change = null;
        change = mapper.readValue("-" + value, SerAssetParameterChange.class);
        Assert.assertEquals(new BigDecimal(value), change.getValue());
        Assert.assertEquals(AssetParameterChangeValueOperation.SUBTRACT, change.getOperation());
    }

    @Test
    public void testSetDeserialization() throws IOException {
        var mapper = getMapperWithDeserializer();
        var value = 15000L;
        SerAssetParameterChange change = null;
        change = mapper.readValue(Long.toString(value), SerAssetParameterChange.class);
        Assert.assertEquals(new BigDecimal(value), change.getValue());
        Assert.assertEquals(AssetParameterChangeValueOperation.SET, change.getOperation());
    }

    private static ObjectMapper getMapperWithDeserializer() {
        var mapper = Main.getObjectMapper();
        var module = new SimpleModule();
        module.addDeserializer(SerAssetParameterChange.class, new SerAssetParameterChangeDeserializer());
        mapper.registerModule(module);
        return mapper;
    }
}
