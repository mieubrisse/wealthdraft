package com.strangegrotto.wealthdraft.backend.tagstores.custom.impl;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.common.base.Preconditions;
import com.strangegrotto.wealthdraft.AbstractYmlBackedStoreFactory;
import com.strangegrotto.wealthdraft.backend.tagstores.custom.api.types.CustomTagDefinition;
import com.strangegrotto.wealthdraft.backend.tagstores.intrinsic.impl.SimpleIntrinsicTagStore;

import java.util.HashMap;
import java.util.Map;

public class SimpleCustomTagStoreFactory extends AbstractYmlBackedStoreFactory<
        Map<String, CustomTagDefinition>,
        Map<String, CustomTagDefinition>,
        SimpleCustomTagStore> {
    private final SimpleIntrinsicTagStore intrinsicTagStore;

    public SimpleCustomTagStoreFactory(ObjectMapper baseMapper, SimpleIntrinsicTagStore intrinsicTagStore) {
        super(baseMapper);
        this.intrinsicTagStore = intrinsicTagStore;
    }

    @Override
    protected void configureMapper(ObjectMapper mapper) {
        return;
    }

    @Override
    protected JavaType getDeserializationType(TypeFactory typeFactory) {
        return typeFactory.constructMapType(HashMap.class, String.class, SerCustomTagDefinition.class);
    }

    @Override
    protected Map<String, CustomTagDefinition> postprocess(Map<String, CustomTagDefinition> input) {
        return input;
    }

    @Override
    protected void validate(Map<String, CustomTagDefinition> postprocessed) {
        var intrinsicTags = this.intrinsicTagStore.getTags();

        for (var customTagEntry : postprocessed.entrySet()) {
            var customTagName = customTagEntry.getKey();
            var customTagDef = customTagEntry.getValue();

            Preconditions.checkState(
                    !intrinsicTags.containsKey(customTagName),
                    "Custom tag name '%s' cannot be used because it collides with an intrinsic tag",
                    customTagName
            );

            var defaultValueOpt = customTagDef.getDefaultValue();
            var allowedValues = customTagDef.getAllowedValues();
            if (defaultValueOpt.isPresent() && allowedValues.size() > 0) {
                var defaultValue = defaultValueOpt.get();
                var allowedValuesStr = String.join(", ", allowedValues);
                Preconditions.checkState(
                        allowedValues.contains(defaultValue),
                        "Default value '%s' isn't contained in allowed values '%s'",
                        defaultValue,
                        allowedValuesStr
                );
            }
        }
    }

    @Override
    protected SimpleCustomTagStore buildResult(Map<String, CustomTagDefinition> postprocessed) {
        return new SimpleCustomTagStore(postprocessed);
    }
}
