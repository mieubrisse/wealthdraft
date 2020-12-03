package com.strangegrotto.wealthdraft.networth.projections;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

// TODO Add a test for this!!!
public class AssetChangeDeserializer extends JsonDeserializer<AssetChange> {
    @Override
    public AssetChange deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
        RawAssetChange rawAssetChange = parser.readValueAs(RawAssetChange.class);
        String raw = rawAssetChange.value;

        AssetChangeValueOperation operation = AssetChangeValueOperation.SET;
        String numericalText = raw;
        if (raw.startsWith("+")) {
            operation = AssetChangeValueOperation.ADD;
            numericalText = raw.substring(1);
        } else if (raw.startsWith("-")) {
            operation = AssetChangeValueOperation.SUBTRACT;
            numericalText = raw.substring(1);
        }

        long value;
        try {
            value = Long.parseLong(numericalText);
        } catch (NumberFormatException e) {
            throw new IOException("Could not parse asset change string '" + raw + "' to long", e);
        }

        return new AssetChange(value, operation);
    }

    private static class RawAssetChange {
        public String value;
    }
}
