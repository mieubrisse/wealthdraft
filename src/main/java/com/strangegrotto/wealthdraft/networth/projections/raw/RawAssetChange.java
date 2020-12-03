package com.strangegrotto.wealthdraft.networth.projections.raw;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.strangegrotto.wealthdraft.errors.ValueOrGError;
import com.strangegrotto.wealthdraft.networth.projections.AssetValueChangeOperation;
import org.immutables.value.Value;

import java.util.function.BiFunction;
import java.util.function.Function;

@Value.Immutable
@JsonDeserialize(as = ImmutableRawAssetChange.class)
public interface RawAssetChange {
    // The change in value of the asset
    String getValue();

    @Value.Derived
    default ValueOrGError<Long> apply(long oldValue) {
        String valueText = getValue();
        AssetValueChangeOperation operation = AssetValueChangeOperation.SET;
        String numericalText = valueText;
        if (valueText.startsWith("+")) {
            operation = AssetValueChangeOperation.ADD;
            numericalText = valueText.substring(1);
        } else if (valueText.startsWith("-")) {
            operation = AssetValueChangeOperation.SUBTRACT;
            numericalText = valueText.substring(1);
        }

        long value;
        try {
            value = Long.parseLong(numericalText);
        } catch (NumberFormatException e) {
            return ValueOrGError.ofNewErr(
                    "Could not parse asset change string, '{}', to long",
                    numericalText
            );
        }

        return ValueOrGError.ofValue(operation.apply(oldValue, value));
    }
}
