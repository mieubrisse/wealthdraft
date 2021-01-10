package com.strangegrotto.wealthdraft.assetfilters;

import com.strangegrotto.wealthdraft.assetimpls.AssetType;
import com.strangegrotto.wealthdraft.assets.definition.Asset;
import com.strangegrotto.wealthdraft.assets.definition.ImmAsset;
import com.strangegrotto.wealthdraft.assets.definition.ImmCustomTagDefinition;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class DisjunctionAssetFilterTest {
    @Test
    public void testVanilla() {
        var needle1Name = "tag1";
        var needle1Value = "tag1value";

        var needle2Name = "tag2";
        var needle2Value = "tag2value";

        var matchingAssetId1 = "matches1";
        var matchingAssetId2 = "matches2";
        var matchingAssetId3 = "matches3";

        var unrelatedTagName = "foo";
        var unrelatedTagValue = "bar";

        var customTags = Map.of(
                needle1Name, ImmCustomTagDefinition.builder().build(),
                needle2Name, ImmCustomTagDefinition.builder().build(),
                unrelatedTagName, ImmCustomTagDefinition.builder().build()
        );

        var haystack = Map.<String, Asset>of(
                matchingAssetId1, ImmAsset.of("Match 1", AssetType.BANK_ACCOUNT).withCustomTags(Map.of(
                        needle1Name, needle1Value
                )),
                matchingAssetId2, ImmAsset.of("Match 2", AssetType.BANK_ACCOUNT).withCustomTags(Map.of(
                        needle1Name, needle1Value,
                        needle2Name, needle2Value
                )),
                matchingAssetId3, ImmAsset.of("Match 3", AssetType.BANK_ACCOUNT).withCustomTags(Map.of(
                        needle2Name, needle2Value
                )),
                "unmatching-tags", ImmAsset.of("Unmatching tags", AssetType.BANK_ACCOUNT).withCustomTags(Map.of(
                        unrelatedTagName, unrelatedTagValue
                )),
                "no-tags", ImmAsset.of("No tags", AssetType.BANK_ACCOUNT)
        );

        var needle1Filter = ImmTagAssetFilter.of(customTags, needle1Name, needle1Value);
        var needle2Filter = ImmTagAssetFilter.of(customTags, needle2Name, needle2Value);
        var disjunctionFilter = ImmDisjunctionAssetFilter.of(List.of(
                needle1Filter,
                needle2Filter
        ));

        var result = disjunctionFilter.apply(Map.of(), haystack);

        var expected = Map.of(
                matchingAssetId1, haystack.get(matchingAssetId1),
                matchingAssetId2, haystack.get(matchingAssetId2),
                matchingAssetId3, haystack.get(matchingAssetId3)
        );
        Assert.assertEquals(expected, result);
    }
}
