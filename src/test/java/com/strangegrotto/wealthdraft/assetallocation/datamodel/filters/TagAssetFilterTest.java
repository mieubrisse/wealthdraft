package com.strangegrotto.wealthdraft.assetallocation.datamodel.filters;

import com.strangegrotto.wealthdraft.assetimpls.AssetTypeTagValue;
import com.strangegrotto.wealthdraft.assetimpls.bankaccount.ImmBankAccountAsset;
import com.strangegrotto.wealthdraft.assets.definition.Asset;
import com.strangegrotto.wealthdraft.assets.definition.CustomTagDefinition;
import com.strangegrotto.wealthdraft.assets.definition.ImmCustomTagDefinition;
import com.strangegrotto.wealthdraft.assets.definition.IntrinsicAssetTag;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;
import java.util.Set;

public class TagAssetFilterTest {
    @Test
    public void testCustomTagWorks() {
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

        var customTags = Map.of(
                selectedTagName, ImmCustomTagDefinition.builder().build()
        );

        var filter = ImmTagAssetFilter.of(customTags, selectedTagName, selectedTagValue);
        var result = filter.apply(assets, assets.keySet());
        var expected = Set.of(matchingAssetId1, matchingAssetId2);
        Assert.assertEquals(expected, result);
    }

    @SuppressFBWarnings(
            value = "RV_RETURN_VALUE_IGNORED",
            justification = "Testing Immutables check method; return value not used"
    )
    @Test
    public void testAssetTypeTagWorks() {
        var tagName = IntrinsicAssetTag.ASSET_TYPE.getTagName();
        //noinspection ResultOfMethodCallIgnored
        ImmTagAssetFilter.of(
                Map.of(),
                tagName,
                AssetTypeTagValue.BANK_ACCOUNT.name()
        );
    }

    @SuppressFBWarnings(
            value = "RV_RETURN_VALUE_IGNORED",
            justification = "Testing Immutables check method; return value not used"
    )
    @Test(expected = IllegalStateException.class)
    public void testErrorOnUnrecognizedCustomName() {
        Map<String, CustomTagDefinition> customTags = Map.of();
        //noinspection ResultOfMethodCallIgnored
        ImmTagAssetFilter.of(
                customTags,
                "unrecognized-tag",
                "some-value"
        );
    }

    @SuppressFBWarnings(
            value = "RV_RETURN_VALUE_IGNORED",
            justification = "Testing Immutables check method; return value not used"
    )
    @Test(expected = IllegalStateException.class)
    public void testErrorOnUrecognizedCustomValue() {
        var customTagName = "customTag";
        Map<String, CustomTagDefinition> customTags = Map.of(
                customTagName, ImmCustomTagDefinition
                        .builder()
                        .addAllowedValues("value1", "value2")
                        .build()
        );
        //noinspection ResultOfMethodCallIgnored
        ImmTagAssetFilter.of(
                customTags,
                customTagName,
                "some-nonexistent-value"
        );
    }

    @SuppressFBWarnings(
            value = "RV_RETURN_VALUE_IGNORED",
            justification = "Testing Immutables check method; return value not used"
    )
    @Test(expected = IllegalStateException.class)
    public void testErrorOnUrecognizedAssetType() {
        var tagName = IntrinsicAssetTag.ASSET_TYPE.getTagName();
        //noinspection ResultOfMethodCallIgnored
        ImmTagAssetFilter.of(
                Map.of(),
                tagName,
                "some-nonexistent-value"
        );
    }
}
