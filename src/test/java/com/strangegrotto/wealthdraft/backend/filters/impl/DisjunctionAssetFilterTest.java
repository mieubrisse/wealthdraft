package com.strangegrotto.wealthdraft.backend.filters.impl;

import com.strangegrotto.wealthdraft.backend.assets.api.types.Asset;
import com.strangegrotto.wealthdraft.backend.assets.api.types.AssetType;
import com.strangegrotto.wealthdraft.backend.assets.api.types.MockAsset;
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

        var haystack = Map.<String, Asset>of(
                matchingAssetId1, new MockAsset("Match 1", AssetType.BANK_ACCOUNT, Map.of(
                        needle1Name, needle1Value
                )),
                matchingAssetId2, new MockAsset("Match 2", AssetType.BANK_ACCOUNT, Map.of(
                        needle1Name, needle1Value,
                        needle2Name, needle2Value
                )),
                matchingAssetId3, new MockAsset("Match 3", AssetType.BANK_ACCOUNT, Map.of(
                        needle2Name, needle2Value
                )),
                "unmatching-tags", new MockAsset("Unmatching tags", AssetType.BANK_ACCOUNT, Map.of(
                        unrelatedTagName, unrelatedTagValue
                )),
                "no-tags", new MockAsset("No tags", AssetType.BANK_ACCOUNT, Map.of())
        );

        var needle1Filter = ImmTagAssetFilter.of(needle1Name, needle1Value);
        var needle2Filter = ImmTagAssetFilter.of(needle2Name, needle2Value);
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
