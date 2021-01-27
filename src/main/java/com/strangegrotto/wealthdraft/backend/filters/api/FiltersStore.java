package com.strangegrotto.wealthdraft.backend.filters.api;

import com.strangegrotto.wealthdraft.backend.filters.api.types.AssetFilter;

import java.util.Map;

public interface FiltersStore {
    Map<String, AssetFilter> getFilters();
}
