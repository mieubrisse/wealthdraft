package com.strangegrotto.wealthdraft.assets.definition;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.collect.ImmutableMap;
import com.strangegrotto.wealthdraft.assetimpls.AssetTypeTagValue;
import com.strangegrotto.wealthdraft.assetimpls.bankaccount.BankAccountAsset;
import com.strangegrotto.wealthdraft.assetimpls.stock.StockAsset;
import com.strangegrotto.wealthdraft.assets.temporal.AssetChange;
import com.strangegrotto.wealthdraft.assets.temporal.AssetSnapshot;

import java.util.Map;
import java.util.stream.Collectors;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
// TODO Rather than these being string-coded values, make them pull from an enum (merge with AssetTypeTagValue)
@JsonSubTypes({
        @JsonSubTypes.Type(value = BankAccountAsset.class, name = "BANK_ACCOUNT"),
        @JsonSubTypes.Type(value = StockAsset.class, name = "STOCK")
})
// TODO Rename this to AbstractAsset and extract Asset to an interface!!
public abstract class Asset<SNAPSHOT extends AssetSnapshot<CHANGE>, CHANGE extends AssetChange> {
    public abstract String getName();

    public abstract Class<CHANGE> getChangeType();

    public abstract Class<SNAPSHOT> getSnapshotType();

    protected abstract AssetTypeTagValue getAssetTypeTagValue();

    protected abstract Map<String, String> getCustomTags();

    public final Map<String, String> getTags() {
        var intrinsicTagsAsStr = getIntrinsicTags().entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().getTagName(),
                        Map.Entry::getValue
                ));
        return ImmutableMap.<String, String>builder()
                .putAll(intrinsicTagsAsStr)
                .putAll(getCustomTags())
                .build();
    }

    // This function is the "registry" of intrinsic tags, so that it's concentrated in one place
    private final Map<IntrinsicAssetTag, String> getIntrinsicTags() {
        return Map.of(
                IntrinsicAssetTag.ASSET_TYPE, getAssetTypeTagValue().name()
        );
    }
}
