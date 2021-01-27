package com.strangegrotto.wealthdraft.backend.filters.impl;

import com.strangegrotto.wealthdraft.backend.assets.api.types.Asset;
import com.strangegrotto.wealthdraft.backend.assets.api.types.AssetType;
import com.strangegrotto.wealthdraft.backend.assets.api.types.MockAsset;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class ConjunctionAssetFilterTest {
    @Test
    public void testVanilla() {
        var needle1Name = "tag1";
        var needle1Value = "tag1value";

        var needle2Name = "tag2";
        var needle2Value = "tag2value";

        var matchingAssetId = "matches";

        var unrelatedTagName = "foo";
        var unrelatedTagValue = "bar";

        var haystack = Map.<String, Asset>of(
                "needle1-but-not-2", new MockAsset("Needle 1 but not 2", AssetType.BANK_ACCOUNT, Map.of(
                        needle1Name, needle1Value
                )),
                matchingAssetId, new MockAsset("Matches", AssetType.BANK_ACCOUNT, Map.of(
                        needle1Name, needle1Value,
                        needle2Name, needle2Value
                )),
                "needle2-but-not-1", new MockAsset("Needle 2 but not 1", AssetType.BANK_ACCOUNT, Map.of(
                        needle2Name, needle2Value
                )),
                "unmatching-tags", new MockAsset("Unmatching tags", AssetType.BANK_ACCOUNT, Map.of(
                        unrelatedTagName, unrelatedTagValue
                )),
                "no-tags", new MockAsset("No tags", AssetType.BANK_ACCOUNT, Map.of())
        );

        var needle1Filter = ImmTagAssetFilter.of(needle1Name, needle1Value);
        var needle2Filter = ImmTagAssetFilter.of(needle2Name, needle2Value);
        var conjunctiveFilter = ImmConjunctionAssetFilter.of(List.of(
                needle1Filter,
                needle2Filter
        ));

        var result = conjunctiveFilter.apply(Map.of(), haystack);

        var expected = Map.of(
                matchingAssetId, haystack.get(matchingAssetId)
        );
        Assert.assertEquals(expected, result);
    }
}
