package com.strangegrotto.wealthdraft.assets.definition;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.strangegrotto.wealthdraft.errors.ValOrGerr;
import com.strangegrotto.wealthdraft.assetimpls.bankaccount.BankAccountAsset;

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
    @JsonIgnore
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

    // TODO This is public only because:
    //  1) the deserializer needs to be able to read this to validate
    //  2) Immutables won't generate the method on the immutable class if it's package-private (unknown why)
    //  The fix is to make the deserializer not deserialize directly to this class!
    @JsonProperty("tags")
    public abstract Map<String, String> getCustomTags();

    protected abstract Map<DefaultAssetTag, String> getDefaultTags();
}
