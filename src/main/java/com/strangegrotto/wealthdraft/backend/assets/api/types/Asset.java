package com.strangegrotto.wealthdraft.backend.assets.api.types;

import java.util.Map;

public interface Asset {
    String getName();

    AssetType getType();

    Map<String, String> getTags();
}
