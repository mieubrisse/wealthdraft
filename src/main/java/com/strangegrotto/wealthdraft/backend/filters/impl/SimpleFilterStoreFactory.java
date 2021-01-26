package com.strangegrotto.wealthdraft.backend.filters.impl;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.common.base.Strings;
import com.strangegrotto.wealthdraft.AbstractYmlBackedStoreFactory;
import com.strangegrotto.wealthdraft.backend.filters.api.types.AssetFilter;
import com.strangegrotto.wealthdraft.backend.tags.custom.api.CustomTagStore;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class SimpleFilterStoreFactory extends AbstractYmlBackedStoreFactory<
        Map<String, ValidatableAssetFilter>,
        Map<String, ValidatableAssetFilter>,
        SimpleFiltersStore> {
    private final CustomTagStore customTagStore;

    public SimpleFilterStoreFactory(ObjectMapper baseMapper, CustomTagStore customTagStore) {
        super(baseMapper);
        this.customTagStore = customTagStore;
    }

    @Override
    protected void configureMapper(ObjectMapper mapper) {
        var module = new SimpleModule();
        module.addDeserializer(
                TagAssetFilter.class,
                new TagAssetFilterDeserializer(this.customTagStore)
        );
        mapper.registerModule(module);
    }

    @Override
    protected JavaType getDeserializationType(TypeFactory typeFactory) {
        return typeFactory.constructMapType(HashMap.class, String.class, SerAssetFilter.class);
    }

    @Override
    protected Map<String, ValidatableAssetFilter> postprocess(Map<String, ValidatableAssetFilter> deserialized) {
        return deserialized;
    }

    @Override
    protected void validate(Map<String, ValidatableAssetFilter> postprocessed) {
        for (var filterEntry : postprocessed.entrySet()) {
            var filterName = filterEntry.getKey();
            var filter = filterEntry.getValue();

            var parentFilters = new LinkedHashSet<>(List.of(filterName));
            var cycleOpt = filter.checkForCycles(postprocessed, parentFilters);
            if (cycleOpt.isPresent()) {
                throw new IllegalStateException(Strings.lenientFormat(
                        "Found an asset filter cycle: %s",
                        String.join(" -> ", cycleOpt.get())
                ));
            }

            // Very important that we validate AFTER we check for cycles to avoid any possible infinite recursions!
            var customTags = this.customTagStore.getTags();
            // TODO Make any exceptions thrown checked
            filter.validate(postprocessed, customTags);
        }
    }

    @Override
    protected SimpleFiltersStore buildResult(Map<String, ValidatableAssetFilter> stringAssetFilterMap) {
        Map<String, AssetFilter> filters = Map.copyOf(stringAssetFilterMap);
        return new SimpleFiltersStore(filters);
    }
}
