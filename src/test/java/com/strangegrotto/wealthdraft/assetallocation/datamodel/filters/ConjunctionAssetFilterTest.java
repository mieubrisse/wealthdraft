package com.strangegrotto.wealthdraft.assetallocation.datamodel.filters;

import com.strangegrotto.wealthdraft.assetimpls.bankaccount.ImmBankAccountAsset;
import com.strangegrotto.wealthdraft.assets.definition.Asset;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConjunctionAssetFilterTest {
    @Test
    public void testVanilla() {
        var needle1Name = "tag1";
        var needle1Value = "tag1value";

        var needle2Name = "tag2";
        var needle2Value = "tag2value";

        var matchingAssetId = "matches";

        var haystack = Map.<String, Asset<?, ?>>of(
                "needle1-but-not-2", ImmBankAccountAsset.of("Needle 1 but not 2").withCustomTags(Map.of(
                        needle1Name, needle1Value
                )),
                matchingAssetId, ImmBankAccountAsset.of("Matches").withCustomTags(Map.of(
                        needle1Name, needle1Value,
                        needle2Name, needle2Value
                )),
                "needle2-but-not-1", ImmBankAccountAsset.of("Needle 2 but not 1").withCustomTags(Map.of(
                        needle2Name, needle2Value
                )),
                "unmatching-tags", ImmBankAccountAsset.of("Unmatching tags").withCustomTags(Map.of("foo", "bar")),
                "no-tags", ImmBankAccountAsset.of("No tags")
        );

        var needle1Filter = ImmTagAssetFilter.of(needle1Name, needle1Value);
        var needle2Filter = ImmTagAssetFilter.of(needle2Name, needle2Value);
        var conjunctiveFilter = ImmConjunctionAssetFilter.of(List.of(
                needle1Filter,
                needle2Filter
        ));

        var result = conjunctiveFilter.apply(haystack, haystack.keySet());

        var expected = Set.of(matchingAssetId);
        Assert.assertEquals(expected, result);
    }
}
