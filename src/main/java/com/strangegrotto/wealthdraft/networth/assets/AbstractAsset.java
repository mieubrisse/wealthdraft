package com.strangegrotto.wealthdraft.networth.assets;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.strangegrotto.wealthdraft.errors.ValOrGerr;
import com.strangegrotto.wealthdraft.networth.BankAccountAsset;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Contains metadata about an asset
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes(
    @JsonSubTypes.Type(name = "BANK_ACCOUNT", value = BankAccountAsset.class)
)
public abstract class AbstractAsset implements Asset {
    @Override
    public final ValOrGerr<Map<AssetTag, String>> getTags() {
        var customTagStrs = getCustomTags();
        var defaultTags = getDefaultTags();
        var defaultTagNames = defaultTags.keySet().stream()
                .map(AssetTag::getName)
                .collect(Collectors.toSet());

        var result = new HashMap<AssetTag, String>(defaultTags);
        for (String customTagName : customTagStrs.keySet()) {
            if (defaultTagNames.contains(customTagName)) {
                return ValOrGerr.newGerr(
                        "Custom tag '{}' collides with default tag of the same name",
                        customTagName
                );
            }
            var customTagObj = new CustomAssetTag(customTagName);
            result.put(customTagObj, customTagStrs.get(customTagName));
        }
        return ValOrGerr.val(result);
    }

    // Visible for the JSON deserializer to verify that all the custom tags are good
    abstract Map<String, String> getCustomTags();

    protected abstract Map<DefaultAssetTag, String> getDefaultTags();
}
