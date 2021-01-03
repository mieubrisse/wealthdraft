package com.strangegrotto.wealthdraft.assets.definition;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.strangegrotto.wealthdraft.assetimpls.AssetTypeTagValue;
import com.strangegrotto.wealthdraft.assetimpls.bankaccount.BankAccountAsset;
import com.strangegrotto.wealthdraft.assetimpls.stock.StockAsset;
import com.strangegrotto.wealthdraft.assets.temporal.AssetChange;
import com.strangegrotto.wealthdraft.assets.temporal.AssetSnapshot;
import com.strangegrotto.wealthdraft.errors.ValOrGerr;

import java.util.Map;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = BankAccountAsset.class, name = "BANK_ACCOUNT"),
        @JsonSubTypes.Type(value = StockAsset.class, name = "STOCK")
})
public interface Asset {
    String getName();

    Class<? extends AssetChange> getChangeType();

    Class<? extends AssetSnapshot> getSnapshotType();

    AssetTypeTagValue getAssetTypeTagValue();

    // If I add more intrinsic tags here, make sure to:
    //  1) update the deserializeEveryAsset test AssetDefinitionsTest to verify it doesn't collide with intrinsic tags
    //  2) update the AssetDefinitions check to verify that custom asset tags don't collide with asset-specific tags

    Map<String, String> getTags();
}
