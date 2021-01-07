package com.strangegrotto.wealthdraft.assetallocation.datamodel.filters;

import com.strangegrotto.wealthdraft.assetimpls.bankaccount.ImmBankAccountAsset;
import com.strangegrotto.wealthdraft.assets.definition.Asset;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;
import java.util.Set;

public class TagAssetFilterTest {
    @Test
    public void testNormal() {
        var selectedTagName = "selectedTag";
        var selectedTagValue = "selectedValue";

        var matchingAssetId1 = "matches1";
        var matchingAssetId2 = "matches2";

        var assets = Map.<String, Asset<?, ?>>of(
                matchingAssetId1, ImmBankAccountAsset.of("Matches 1").withCustomTags(Map.of(
                        selectedTagName, selectedTagValue
                )),
                "right-name-wrong-values", ImmBankAccountAsset.of("Right name, wrong values").withCustomTags(Map.of(
                        selectedTagName, "wrong-value"
                )),
                "unmatching-tags", ImmBankAccountAsset.of("Unmatching tags").withCustomTags(Map.of("foo", "bar")),
                "no-tags", ImmBankAccountAsset.of("No tags"),
                matchingAssetId2, ImmBankAccountAsset.of("Matches 2").withCustomTags(Map.of(
                        selectedTagName, selectedTagValue
                ))
        );

        var filter = ImmTagAssetFilter.of(selectedTagName, selectedTagValue);
        var result = filter.apply(assets, assets.keySet());
        var expected = Set.of(matchingAssetId1, matchingAssetId2);
        Assert.assertEquals(expected, result);

    }
}
