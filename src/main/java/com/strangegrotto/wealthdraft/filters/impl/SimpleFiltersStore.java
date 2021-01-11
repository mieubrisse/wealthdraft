package com.strangegrotto.wealthdraft.filters.impl;

import com.strangegrotto.wealthdraft.filters.api.FiltersStore;
import com.strangegrotto.wealthdraft.filters.api.types.AssetFilter;

import java.util.Map;

public class SimpleFiltersStore implements FiltersStore {
    private final Map<String, AssetFilter> filters;

    public SimpleFiltersStore(Map<String, AssetFilter> filters) {
        this.filters = Map.copyOf(filters);
    }

    @Override
    public Map<String, AssetFilter> getFilters() {
        return this.filters;
    }
}
