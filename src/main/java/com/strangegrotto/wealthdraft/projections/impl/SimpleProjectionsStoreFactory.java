package com.strangegrotto.wealthdraft.projections.impl;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.strangegrotto.wealthdraft.AbstractYmlBackedStoreFactory;

public class SimpleProjectionsStoreFactory implements AbstractYmlBackedStoreFactory<
        SerProjections,
        SerProjections,
        SimpleProjectionsStore> {

    @Override
    protected void configureMapper(ObjectMapper mapper) {

    }

    @Override
    protected JavaType getDeserializationType(TypeFactory typeFactory) {
        return null;
    }

    @Override
    protected SerProjections postprocess(SerProjections projections) {
        // TODO unroll scenarios
    }

    @Override
    protected void validate(SerProjections projections) {

    }

    @Override
    protected SimpleProjectionsStore buildResult(SerProjections projections) {
        return null;
    }
}
