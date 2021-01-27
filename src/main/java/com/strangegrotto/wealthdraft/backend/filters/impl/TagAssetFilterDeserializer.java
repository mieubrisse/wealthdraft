package com.strangegrotto.wealthdraft.backend.filters.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.strangegrotto.wealthdraft.backend.tags.custom.api.CustomTagStore;
import com.strangegrotto.wealthdraft.backend.tags.intrinsic.IntrinsicAssetTag;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class TagAssetFilterDeserializer extends JsonDeserializer<TagAssetFilter> {
    private static class RawTagAssetFilter {
        @JsonProperty("tag")
        public String tag;

        @JsonProperty("value")
        public String value;
    }

    private final CustomTagStore customTagStore;

    public TagAssetFilterDeserializer(CustomTagStore customTagStore) {
        this.customTagStore = customTagStore;
    }

    @Override
    public TagAssetFilter deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        var raw = p.readValueAs(RawTagAssetFilter.class);
        validateRaw(raw);
        return ImmTagAssetFilter.of(
            raw.tag,
            raw.value
        );
    }

    private void validateRaw(RawTagAssetFilter raw) {
        Map<String, Set<String>> intrinsicTagNamesAndValues = IntrinsicAssetTag.getTagNamesToAllowedValues();

        var customTags = this.customTagStore.getTags();
        Set<String> customTagNames = customTags.keySet();
        Set<String> intrinsicTagNames = intrinsicTagNamesAndValues.keySet();

        Set<String> allTagNames = new HashSet<>();
        allTagNames.addAll(intrinsicTagNames);
        allTagNames.addAll(customTagNames);

        var tagName = raw.tag;
        Preconditions.checkState(
                allTagNames.contains(tagName),
                "Tag '%s' is neither an instrinsic nor custom tag",
                tagName
        );

        var tagValue = raw.value;
        if (intrinsicTagNames.contains(tagName)) {
            var allowedValues = intrinsicTagNamesAndValues.get(tagName);
            if (allowedValues.size() > 0) {
                Preconditions.checkState(
                        allowedValues.contains(tagValue),
                        "Value '%s' is not in allowed range [%s] for intrinsic tag '%s'",
                        tagValue,
                        String.join(", ", allowedValues),
                        tagName
                );
            }
        } else if (customTagNames.contains(tagName)) {
            var customDefinition = customTags.get(tagName);
            var allowedValues = customDefinition.getAllowedValues();
            if (allowedValues.size() > 0) {
                Preconditions.checkState(
                        allowedValues.contains(tagValue),
                        "Tag '%s' only allows certain values and '%s' isn't one of them",
                        tagValue
                );
            }
        } else {
            throw new IllegalStateException(Strings.lenientFormat(
                    "Tag '%s' is neither intrinsic nor custom, EVEN AFTER we checked whether it is - this is very weird!",
                    tagName
            ));
        }
    }
}
