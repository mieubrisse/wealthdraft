package com.strangegrotto.wealthdraft.assetfilters;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.strangegrotto.wealthdraft.assets.definition.CustomTagDefinition;

import java.io.IOException;
import java.util.Map;

public class TagAssetFilterDeserializer extends JsonDeserializer<TagAssetFilter> {
    private static class RawTagAssetFilter {
        @JsonProperty("tag")
        public String tag;

        @JsonProperty("value")
        public String value;
    }

    private final Map<String, CustomTagDefinition> customTags;

    public TagAssetFilterDeserializer(Map<String, CustomTagDefinition> customTags) {
        this.customTags = customTags;
    }

    @Override
    public TagAssetFilter deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        var raw = p.readValueAs(RawTagAssetFilter.class);
        return ImmTagAssetFilter.of(
            this.customTags,
            raw.tag,
            raw.value
        );
    }
}
