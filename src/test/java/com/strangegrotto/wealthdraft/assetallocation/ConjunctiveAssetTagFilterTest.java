package com.strangegrotto.wealthdraft.assetallocation;

import com.strangegrotto.wealthdraft.assetimpls.bankaccount.ImmBankAccountAsset;
import com.strangegrotto.wealthdraft.assets.definition.Asset;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class ConjunctiveAssetTagFilterTest {
    @Test
    public void testVanilla() {
        var selectedTagName = "selectedTag";
        var selectedTagValue = "selectedValue";

        var matchingAssetId = "matches";

        var assets = Map.<String, Asset<?, ?>>of(
                matchingAssetId, ImmBankAccountAsset.of("Matches").withTags(Map.of(
                        selectedTagName, selectedTagValue
                )),
                "right-name-wrong-values", ImmBankAccountAsset.of("Right name, wrong values").withTags(Map.of(
                        selectedTagName, "wrong-value"
                )),
                "unmatching-tags", ImmBankAccountAsset.of("Unmatching tags").withTags(Map.of("foo", "bar")),
                "no-tags", ImmBankAccountAsset.of("No tags")
        );

        var filter = ImmConjunctiveAssetTagFilter.of(Map.of(selectedTagName, selectedTagValue));
        var output = filter.apply(assets);

        Assert.assertEquals(1, output.size());
        Assert.assertTrue(output.containsKey(matchingAssetId));
    }
}
