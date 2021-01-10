package com.strangegrotto.wealthdraft.assetfilters;

import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import com.strangegrotto.wealthdraft.assetallocation.TargetAssetAllocationsTestFiles;
import com.strangegrotto.wealthdraft.assetimpls.AssetType;
import com.strangegrotto.wealthdraft.assets.definition.Asset;
import com.strangegrotto.wealthdraft.assets.definition.ImmAsset;
import com.strangegrotto.wealthdraft.assets.definition.ImmCustomTagDefinition;
import com.strangegrotto.wealthdraft.assetfilters.filters.ImmEmbeddedFilterAssetFilter;
import com.strangegrotto.wealthdraft.assetfilters.filters.ImmTagAssetFilter;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
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
        Map<String, Asset> haystack = Map.of(
                matchingAssetId, ImmAsset.of("Matching", AssetType.BANK_ACCOUNT).withCustomTags(Map.of(
                        needleName, needleValue
                )),
                "unmatchingAsset", ImmAsset.of("Doesn't match", AssetType.BANK_ACCOUNT)
        );

        var expected = Map.of(
                matchingAssetId, haystack.get(matchingAssetId)
        );
        var actual = embeddingFilter.apply(filters, haystack);

        Assert.assertEquals(expected, actual);
    }

    @Test(expected = IllegalStateException.class)
    public void testErrorOnNonexistentEmbeddedFilter() {
        var embeddingFilterId = "embedding";
        var embeddingFilter = ImmEmbeddedFilterAssetFilter.of("this-filter-id-doesnt-exist");
        Map<String, AssetFilter> filters = Map.of(
                embeddingFilterId, embeddingFilter
        );

        Map<String, Asset> assets = Map.of(
                "someAsset", ImmAsset.of("Some ranodm asset", AssetType.BANK_ACCOUNT)
        );

        embeddingFilter.apply(filters, assets);
    }

    @Test(expected = IllegalStateException.class)
    public void testErrorOnFilterCycle() throws IOException {
        var filterId1 = "filter1";
        var filterId2 = "filter2";
        var filterId3 = "filter3";

        var filter1 = ImmEmbeddedFilterAssetFilter.of(filterId2);

        Map<String, AssetFilter> filters = Map.of(
                filterId1, filter1,
                filterId2, ImmEmbeddedFilterAssetFilter.of(filterId3),
                filterId3, ImmEmbeddedFilterAssetFilter.of(filterId1)
        );

        Map<String, Asset> assets = Map.of(
                "someAsset", ImmAsset.of("Some random asset", AssetType.BANK_ACCOUNT)
        );

        filter1.apply(filters, assets);
    }
}
