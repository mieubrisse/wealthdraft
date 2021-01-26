package com.strangegrotto.wealthdraft.backend.tagstores.custom.impl;

import com.strangegrotto.wealthdraft.backend.tagstores.custom.api.types.CustomTagDefinition;

import java.util.Map;

public class ExpectedExampleCustomTags {
    // Custom tags
    public static final String DOM_OR_INTL_TAG = "domOrIntl";
    public static final String DOMESTIC_ASSET_TAG_VALUE = "domestic";
    public static final String INTERNATIONAL_ASSET_TAG_VALUE = "international";

    public static final String BROKER_TAG = "broker";

    public static final Map<String, CustomTagDefinition> EXPECTED_CUSTOM_TAGS = Map.of(
            DOM_OR_INTL_TAG, ImmSerCustomTagDefinition.builder()
                    .addAllowedValues(DOMESTIC_ASSET_TAG_VALUE, INTERNATIONAL_ASSET_TAG_VALUE)
                    .defaultValue(DOMESTIC_ASSET_TAG_VALUE)
                    .required(true)
                    .build(),
            BROKER_TAG, ImmSerCustomTagDefinition.builder().build()
    );
}
