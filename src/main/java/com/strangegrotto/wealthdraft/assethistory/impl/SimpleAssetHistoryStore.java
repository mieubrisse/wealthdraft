package com.strangegrotto.wealthdraft.assethistory.impl;

import com.strangegrotto.wealthdraft.assethistory.api.AssetHistoryStore;
import com.strangegrotto.wealthdraft.assethistory.api.types.AssetSnapshot;

import java.time.LocalDate;
import java.util.Map;
import java.util.SortedMap;

public class SimpleAssetHistoryStore implements AssetHistoryStore {
    private final SortedMap<LocalDate, Map<String, AssetSnapshot<?>>> history;

    public SimpleAssetHistoryStore(SortedMap<LocalDate, Map<String, AssetSnapshot<?>>> history) {
        this.history = history;
    }

    @Override
    public SortedMap<LocalDate, Map<String, AssetSnapshot<?>>> getHistory() {
        return null;
    }
}
