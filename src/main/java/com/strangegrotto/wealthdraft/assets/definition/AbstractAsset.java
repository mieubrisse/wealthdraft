package com.strangegrotto.wealthdraft.assets.definition;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.strangegrotto.wealthdraft.assetimpls.bankaccount.BankAccountAsset;
import org.immutables.value.Value;

import java.util.HashMap;
import java.util.Map;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes(
        @JsonSubTypes.Type(value = BankAccountAsset.class, name = "BANK_ACCOUNT")
)
public abstract class AbstractAsset implements Asset {
    private final String name;
    private final Map<String, String> customTags;

    public AbstractAsset(String name, Map<String, String> customTags) {
        this.name = name;
        this.customTags = customTags;
    }

    @Override
    public final String getName() {
        return this.name;
    }

    @Override
    public final Map<String, String> getCustomTags() {
        return this.customTags;
    }
}
