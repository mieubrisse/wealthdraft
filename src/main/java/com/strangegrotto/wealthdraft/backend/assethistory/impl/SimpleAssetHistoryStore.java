package com.strangegrotto.wealthdraft.backend.assethistory.impl;

import com.google.common.collect.ImmutableSortedMap;
import com.strangegrotto.wealthdraft.backend.assethistory.api.AssetHistoryStore;
import com.strangegrotto.wealthdraft.backend.assethistory.api.types.AssetSnapshot;

import java.time.LocalDate;
import java.util.Map;
import java.util.SortedMap;

public class SimpleAssetHistoryStore implements AssetHistoryStore {
    private final SortedMap<LocalDate, Map<String, AssetSnapshot<?>>> history;

    public SimpleAssetHistoryStore(SortedMap<LocalDate, Map<String, AssetSnapshot<?>>> history) {
        this.history = ImmutableSortedMap.copyOf(history);
    }

    @Override
    public SortedMap<LocalDate, Map<String, AssetSnapshot<?>>> getHistory() {
        return this.history;
    }
}
