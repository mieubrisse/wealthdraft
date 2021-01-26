package com.strangegrotto.wealthdraft.backend.filters.impl;

import com.google.common.collect.Sets;
import com.strangegrotto.wealthdraft.backend.assets.api.types.Asset;
import com.strangegrotto.wealthdraft.backend.assets.api.types.AssetType;
import com.strangegrotto.wealthdraft.backend.assets.api.types.MockAsset;
import com.strangegrotto.wealthdraft.backend.filters.api.types.AssetFilter;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class EmbeddedFilterAssetFilterTest {
    @Test
    public void testNormalOperation() {
        var needleName = "myTag";
        var needleValue = "myTagValue";

        var embeddedFilterId = "embedded";
        var embeddedFilter = ImmTagAssetFilter.of(
                needleName,
                needleValue
        );
        var embeddingFilterId = "embedding";
        var embeddingFilter = ImmEmbeddedFilterAssetFilter.of(embeddedFilterId);
        var filters = Map.<String, AssetFilter>of(
                embeddedFilterId, embeddedFilter,
                embeddingFilterId, embeddingFilter
        );

        var matchingAssetId = "matching";
        var haystack = Map.<String, Asset>of(
                matchingAssetId, new MockAsset("Matching", AssetType.BANK_ACCOUNT, Map.of(
                        needleName, needleValue
                )),
                "unmatchingAsset", new MockAsset("Doesn't match", AssetType.BANK_ACCOUNT, Map.of())
        );

        var expected = Map.of(
                matchingAssetId, haystack.get(matchingAssetId)
        );
        var actual = embeddingFilter.apply(filters, haystack);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testCycleDetection() throws IOException {
        var filterId1 = "filter1";
        var filterId2 = "filter2";
        var filterId3 = "filter3";

        var filter1 = ImmEmbeddedFilterAssetFilter.of(filterId2);

        var filters = Map.<String, ValidatableAssetFilter>of(
                filterId1, filter1,
                filterId2, ImmEmbeddedFilterAssetFilter.of(filterId3),
                filterId3, ImmEmbeddedFilterAssetFilter.of(filterId1)
        );

        var parentFilters = Sets.newLinkedHashSet(List.of(filterId1));
        filter1.checkForCycles(filters, parentFilters);
    }

    @Test(expected = IllegalStateException.class)
    public void testErrorOnNonexistentEmbeddedFilter() {
        var embeddingFilterId = "embedding";
        var embeddingFilter = ImmEmbeddedFilterAssetFilter.of("this-filter-id-doesnt-exist");
        var filters = Map.<String, AssetFilter>of(
                embeddingFilterId, embeddingFilter
        );

        var assets = Map.<String, Asset>of(
                "someAsset", new MockAsset("Some random asset", AssetType.BANK_ACCOUNT, Map.of())
        );

        embeddingFilter.apply(filters, assets);
    }

}
