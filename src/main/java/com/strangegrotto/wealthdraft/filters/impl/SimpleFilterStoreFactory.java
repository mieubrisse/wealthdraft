package com.strangegrotto.wealthdraft.filters.impl;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.common.base.Strings;
import com.strangegrotto.wealthdraft.AbstractYmlBackedStoreFactory;
import com.strangegrotto.wealthdraft.filters.api.types.AssetFilter;
import com.strangegrotto.wealthdraft.tagstores.custom.api.CustomTagStore;
import com.strangegrotto.wealthdraft.tagstores.intrinsic.IntrinsicTagStore;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class SimpleFilterStoreFactory implements AbstractYmlBackedStoreFactory<
        Map<String, AssetFilter>,
        Map<String, AssetFilter>,
        SimpleFiltersStore> {
    private final CustomTagStore customTagStore;
    private final IntrinsicTagStore intrinsicTagStore;

    public SimpleFilterStoreFactory(CustomTagStore customTagStore, IntrinsicTagStore intrinsicTagStore) {
        this.customTagStore = customTagStore;
        this.intrinsicTagStore = intrinsicTagStore;
    }

    @Override
    protected void configureMapper(ObjectMapper mapper) {
        var module = new SimpleModule();
        module.addDeserializer(
                TagAssetFilter.class,
                new TagAssetFilterDeserializer(this.customTagStore, this.intrinsicTagStore)
        );
        mapper.registerModule(module);
    }

    @Override
    protected JavaType getDeserializationType(TypeFactory typeFactory) {
        return typeFactory.constructMapType(HashMap.class, String.class, SerAssetFilter.class);
    }

    @Override
    protected Map<String, AssetFilter> postprocess(Map<String, AssetFilter> deserialized) {
        return deserialized;
    }

    @Override
    protected void validate(Map<String, AssetFilter> postprocessed) {
        // TODO This is a terrible spot to do error-checking!!
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
        }
    }

    @Override
    protected SimpleFiltersStore buildResult(Map<String, AssetFilter> stringAssetFilterMap) {
        return new SimpleFiltersStore(stringAssetFilterMap);
    }
}
