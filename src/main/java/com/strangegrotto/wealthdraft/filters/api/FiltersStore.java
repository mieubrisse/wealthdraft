package com.strangegrotto.wealthdraft.filters.api;

import com.strangegrotto.wealthdraft.filters.api.types.AssetFilter;
import com.strangegrotto.wealthdraft.filters.impl.SerAssetFilter;

import java.util.Map;

public interface FiltersStore {
    Map<String, AssetFilter> getFilters();
}
