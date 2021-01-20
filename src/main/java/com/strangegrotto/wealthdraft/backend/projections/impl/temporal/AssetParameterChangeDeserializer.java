package com.strangegrotto.wealthdraft.backend.projections.impl.temporal;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.math.BigDecimal;

public class AssetParameterChangeDeserializer extends JsonDeserializer<SerAssetParameterChange> {
    @Override
    public SerAssetParameterChange deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
        String raw = parser.readValueAs(String.class);

        AssetParameterChangeValueOperation operation = AssetParameterChangeValueOperation.SET;
        String numericalText = raw;
        if (raw.startsWith("+")) {
            operation = AssetParameterChangeValueOperation.ADD;
            numericalText = raw.substring(1);
        } else if (raw.startsWith("-")) {
            operation = AssetParameterChangeValueOperation.SUBTRACT;
            numericalText = raw.substring(1);
        }

        BigDecimal value;
        try {
            value = new BigDecimal(numericalText);
        } catch (NumberFormatException e) {
            throw new IOException("Could not parse asset change string '" + raw + "' to BigDecimal", e);
        }

        return ImmSerAssetParameterChange.of(value, operation);
    }
}
