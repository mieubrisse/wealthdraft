package com.strangegrotto.wealthdraft.backend.filters;

import com.strangegrotto.wealthdraft.backend.assets.api.types.AssetType;
import com.strangegrotto.wealthdraft.backend.assets.impl.ImmSerAsset;
import com.strangegrotto.wealthdraft.backend.assets.impl.SerAsset;
import com.strangegrotto.wealthdraft.backend.assets.impl.ImmCustomTagDefinition;
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

        var customTags = Map.of(
                needle1Name, ImmCustomTagDefinition.builder().build(),
                needle2Name, ImmCustomTagDefinition.builder().build(),
                unrelatedTagName, ImmCustomTagDefinition.builder().build()
        );

        var haystack = Map.<String, SerAsset>of(
                "needle1-but-not-2", ImmSerAsset.of("Needle 1 but not 2", AssetType.BANK_ACCOUNT).withCustomTags(Map.of(
                        needle1Name, needle1Value
                )),
                matchingAssetId, ImmSerAsset.of("Matches", AssetType.BANK_ACCOUNT).withCustomTags(Map.of(
                        needle1Name, needle1Value,
                        needle2Name, needle2Value
                )),
                "needle2-but-not-1", ImmSerAsset.of("Needle 2 but not 1", AssetType.BANK_ACCOUNT).withCustomTags(Map.of(
                        needle2Name, needle2Value
                )),
                "unmatching-tags", ImmSerAsset.of("Unmatching tags", AssetType.BANK_ACCOUNT).withCustomTags(Map.of(
                        unrelatedTagName, unrelatedTagValue
                )),
                "no-tags", ImmSerAsset.of("No tags", AssetType.BANK_ACCOUNT)
        );

        var needle1Filter = ImmTagAssetFilter.of(customTags, needle1Name, needle1Value);
        var needle2Filter = ImmTagAssetFilter.of(customTags, needle2Name, needle2Value);
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
