package com.strangegrotto.wealthdraft.assetallocation.datamodel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import com.strangegrotto.wealthdraft.assetfilters.AssetFilter;
import org.immutables.value.Value;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class TargetAssetAllocationsDeserializer extends JsonDeserializer<TargetAssetAllocations> {
    @WealthdraftImmutableStyle
    @Value.Immutable
    @JsonDeserialize(as = ImmRawTargetAssetAllocations.class)
    interface RawTargetAssetAllocations {
        @JsonProperty("targets")
        List<TargetAssetAllocation> getTargets();
    }

    private final Map<String, AssetFilter> filters;

    public TargetAssetAllocationsDeserializer(Map<String, AssetFilter> filters) {
        this.filters = filters;
    }

    @Override
    public TargetAssetAllocations deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        var rawAssetAllocations = p.readValuesAs(RawTargetAssetAllocations.class);

        return ImmTargetAssetAllocations.of(
                this.filters,
                rawAssetAllocations.getTargets()
        );
    }
}
