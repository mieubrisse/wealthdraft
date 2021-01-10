package com.strangegrotto.wealthdraft.assetallocation.datamodel.filters;

import com.strangegrotto.wealthdraft.assetimpls.AssetType;
import com.strangegrotto.wealthdraft.assets.definition.Asset;
import com.strangegrotto.wealthdraft.assets.definition.ImmAsset;
import com.strangegrotto.wealthdraft.assets.definition.ImmCustomTagDefinition;
import org.junit.Assert;
import org.junit.Test;

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
}
