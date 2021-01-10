package com.strangegrotto.wealthdraft.assetallocation.datamodel.filters;

import com.strangegrotto.wealthdraft.assetimpls.AssetType;
import com.strangegrotto.wealthdraft.assets.definition.Asset;
import com.strangegrotto.wealthdraft.assets.definition.ImmAsset;
import com.strangegrotto.wealthdraft.assets.definition.ImmCustomTagDefinition;
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

        var unrelatedTagName = "foo";
        var unrelatedTagValue = "bar";

        var customTags = Map.of(
                needle1Name, ImmCustomTagDefinition.builder().build(),
                needle2Name, ImmCustomTagDefinition.builder().build(),
                unrelatedTagName, ImmCustomTagDefinition.builder().build()
        );

        var haystack = Map.<String, Asset>of(
                "needle1-but-not-2", ImmAsset.of("Needle 1 but not 2", AssetType.BANK_ACCOUNT).withCustomTags(Map.of(
                        needle1Name, needle1Value
                )),
                matchingAssetId, ImmAsset.of("Matches", AssetType.BANK_ACCOUNT).withCustomTags(Map.of(
                        needle1Name, needle1Value,
                        needle2Name, needle2Value
                )),
                "needle2-but-not-1", ImmAsset.of("Needle 2 but not 1", AssetType.BANK_ACCOUNT).withCustomTags(Map.of(
                        needle2Name, needle2Value
                )),
                "unmatching-tags", ImmAsset.of("Unmatching tags", AssetType.BANK_ACCOUNT).withCustomTags(Map.of(
                        unrelatedTagName, unrelatedTagValue
                )),
                "no-tags", ImmAsset.of("No tags", AssetType.BANK_ACCOUNT)
        );

        var needle1Filter = ImmTagAssetFilter.of(customTags, needle1Name, needle1Value);
        var needle2Filter = ImmTagAssetFilter.of(customTags, needle2Name, needle2Value);
        var conjunctiveFilter = ImmConjunctionAssetFilter.of(List.of(
                needle1Filter,
                needle2Filter
        ));

        var result = conjunctiveFilter.apply(haystack);

        var expected = Map.of(
                matchingAssetId, haystack.get(matchingAssetId)
        );
        Assert.assertEquals(expected, result);
    }
}
