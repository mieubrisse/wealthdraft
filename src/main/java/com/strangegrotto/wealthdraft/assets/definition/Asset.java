package com.strangegrotto.wealthdraft.assets.definition;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.strangegrotto.wealthdraft.assets.temporal.AssetChange;
import com.strangegrotto.wealthdraft.assets.temporal.AssetSnapshot;
import com.strangegrotto.wealthdraft.errors.ValOrGerr;

import java.util.Map;

@JsonDeserialize(as = AbstractAsset.class)
public interface Asset {
    String getName();

    Class<? extends AssetChange> getChangeType();

    Class<? extends AssetSnapshot> getSnapshotType();

    ValOrGerr<Map<AssetTag, String>> getTags();
}
