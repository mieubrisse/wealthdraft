package com.strangegrotto.wealthdraft.assetallocation.datamodel.filters;

import com.strangegrotto.wealthdraft.assetimpls.bankaccount.ImmBankAccountAsset;
import com.strangegrotto.wealthdraft.assets.definition.Asset;
import com.strangegrotto.wealthdraft.assets.definition.CustomTagDefinition;
import com.strangegrotto.wealthdraft.assets.definition.ImmCustomTagDefinition;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

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

        var haystack = Map.<String, Asset<?, ?>>of(
                matchingAssetId1, ImmBankAccountAsset.of("Match 1").withCustomTags(Map.of(
                        needle1Name, needle1Value
                )),
                matchingAssetId2, ImmBankAccountAsset.of("Match 2").withCustomTags(Map.of(
                        needle1Name, needle1Value,
                        needle2Name, needle2Value
                )),
                matchingAssetId3, ImmBankAccountAsset.of("Match 3").withCustomTags(Map.of(
                        needle2Name, needle2Value
                )),
                "unmatching-tags", ImmBankAccountAsset.of("Unmatching tags").withCustomTags(Map.of(
                        unrelatedTagName, unrelatedTagValue
                )),
                "no-tags", ImmBankAccountAsset.of("No tags")
        );

        var needle1Filter = ImmTagAssetFilter.of(customTags, needle1Name, needle1Value);
        var needle2Filter = ImmTagAssetFilter.of(customTags, needle2Name, needle2Value);
        var disjunctionFilter = ImmDisjunctionAssetFilter.of(List.of(
                needle1Filter,
                needle2Filter
        ));

        var result = disjunctionFilter.apply(haystack, haystack.keySet());

        var expected = Set.of(matchingAssetId1, matchingAssetId2, matchingAssetId3);
        Assert.assertEquals(expected, result);
    }
}
