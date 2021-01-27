package com.strangegrotto.wealthdraft.backend.tags.custom.impl;

import com.strangegrotto.wealthdraft.backend.tags.custom.api.types.CustomTagDefinition;

import java.util.Map;

public class ExpectedExampleCustomTags {
    // Custom tags
    public static final String DOM_OR_INTL_TAG = "domOrIntl";
    public static final String DOMESTIC_ASSET_TAG_VALUE = "domestic";
    public static final String INTERNATIONAL_ASSET_TAG_VALUE = "international";

    public static final String BROKER_TAG = "broker";

    public static final String IS_RETIREMENT_TAG = "isRetirement";
    public static final String IS_RETIREMENT_TAG_VALUE = "true";
    public static final String IS_NOT_RETIREMENT_TAG_VALUE = "false";

    public static final Map<String, CustomTagDefinition> EXPECTED_CUSTOM_TAGS = Map.of(
            DOM_OR_INTL_TAG, ImmSerCustomTagDefinition.builder()
                    .addAllowedValues(DOMESTIC_ASSET_TAG_VALUE, INTERNATIONAL_ASSET_TAG_VALUE)
                    .defaultValue(DOMESTIC_ASSET_TAG_VALUE)
                    .required(false)
                    .build(),
            BROKER_TAG, ImmSerCustomTagDefinition.builder().build(),
            IS_RETIREMENT_TAG, ImmSerCustomTagDefinition.builder()
                    .addAllowedValues(IS_RETIREMENT_TAG_VALUE, IS_NOT_RETIREMENT_TAG_VALUE)
                    .required(true)
                    .build()
    );
}
