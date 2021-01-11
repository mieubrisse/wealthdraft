package com.strangegrotto.wealthdraft.assethistory.api;

import com.strangegrotto.wealthdraft.assethistory.api.types.AssetSnapshot;

import java.time.LocalDate;
import java.util.Map;
import java.util.SortedMap;

public interface AssetHistoryStore {
    SortedMap<LocalDate, Map<String, AssetSnapshot<?>>> getHistory();
}
