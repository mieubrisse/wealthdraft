package com.strangegrotto.wealthdraft.assetfilters;

import com.google.common.collect.Sets;
import com.strangegrotto.wealthdraft.assets.api.types.AssetType;
import com.strangegrotto.wealthdraft.assets.impl.ImmSerAsset;
import com.strangegrotto.wealthdraft.assets.impl.SerAsset;
import com.strangegrotto.wealthdraft.assets.impl.ImmCustomTagDefinition;
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

        var tags = Map.of(
                needleName, ImmCustomTagDefinition.builder().build()
        );

        var embeddedFilterId = "embedded";
        var embeddedFilter = ImmTagAssetFilter.of(
                tags,
                needleName,
                needleValue
        );
        var embeddingFilterId = "embedding";
        var embeddingFilter = ImmEmbeddedFilterAssetFilter.of(embeddedFilterId);
        var filters = Map.of(
                embeddedFilterId, embeddedFilter,
                embeddingFilterId, embeddingFilter
        );

        var matchingAssetId = "matching";
        Map<String, SerAsset> haystack = Map.of(
                matchingAssetId, ImmSerAsset.of("Matching", AssetType.BANK_ACCOUNT).withCustomTags(Map.of(
                        needleName, needleValue
                )),
                "unmatchingAsset", ImmSerAsset.of("Doesn't match", AssetType.BANK_ACCOUNT)
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

        Map<String, AssetFilter> filters = Map.of(
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
        Map<String, AssetFilter> filters = Map.of(
                embeddingFilterId, embeddingFilter
        );

        Map<String, SerAsset> assets = Map.of(
                "someAsset", ImmSerAsset.of("Some ranodm asset", AssetType.BANK_ACCOUNT)
        );

        embeddingFilter.apply(filters, assets);
    }

}
