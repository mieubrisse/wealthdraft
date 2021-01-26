package com.strangegrotto.wealthdraft;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public abstract class AbstractYmlBackedStoreFactory<DESERIALIZED, POSTPROCESSED, OUTPUT> {
    private final ObjectMapper baseMapper;

    public AbstractYmlBackedStoreFactory(ObjectMapper baseMapper) {
        this.baseMapper = baseMapper;
    }

    public final OUTPUT create(URL ymlFileUrl) throws IOException {
        var mapper = this.baseMapper.copy();
        configureMapper(mapper);

        var type = getDeserializationType(mapper.getTypeFactory());
        DESERIALIZED deserialized = mapper.readValue(ymlFileUrl, type);
        POSTPROCESSED postprocessed = postprocess(deserialized);
        validate(postprocessed);
        return buildResult(postprocessed);
    }

    protected abstract void configureMapper(ObjectMapper mapper);

    protected abstract JavaType getDeserializationType(TypeFactory typeFactory);

    protected abstract POSTPROCESSED postprocess(DESERIALIZED deserialized);

    // TODO make the return type an error?
    protected abstract void validate(POSTPROCESSED postprocessed);

    protected abstract OUTPUT buildResult(POSTPROCESSED postprocessed);
}
