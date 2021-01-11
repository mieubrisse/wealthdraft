package com.strangegrotto.wealthdraft.filters.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import com.strangegrotto.wealthdraft.assets.api.types.AssetType;
import com.strangegrotto.wealthdraft.filters.ImmTagAssetFilter;
import com.strangegrotto.wealthdraft.tagstores.custom.api.CustomTagStore;
import com.strangegrotto.wealthdraft.tagstores.custom.api.types.CustomTagDefinition;
import com.strangegrotto.wealthdraft.tagstores.intrinsic.IntrinsicAssetTag;
import com.strangegrotto.wealthdraft.tagstores.intrinsic.IntrinsicTagStore;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TagAssetFilterDeserializer extends JsonDeserializer<TagAssetFilter> {
    private static class RawTagAssetFilter {
        @JsonProperty("tag")
        public String tag;

        @JsonProperty("value")
        public String value;
    }

    private final CustomTagStore customTagStore;
    private final IntrinsicTagStore intrinsicTagStore;

    public TagAssetFilterDeserializer(CustomTagStore customTagStore, IntrinsicTagStore intrinsicTagStore) {
        this.customTagStore = customTagStore;
        this.intrinsicTagStore = intrinsicTagStore;
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
        var customTags = this.customTagStore.getTags();
        var intrinsicTags = this.intrinsicTagStore.getTags();

        Set<String> customTagNames = customTags.keySet();
        Set<String> intrinsicTagNames = intrinsicTags.keySet();

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
            var allowedValues = intrinsicTags.get(tagName);
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
