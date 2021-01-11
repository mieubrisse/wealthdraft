package com.strangegrotto.wealthdraft.assetfilters;

import com.strangegrotto.wealthdraft.assets.api.types.AssetType;
import com.strangegrotto.wealthdraft.assets.impl.*;
import com.strangegrotto.wealthdraft.tagstores.custom.api.types.CustomTagDefinition;
import com.strangegrotto.wealthdraft.tagstores.intrinsic.IntrinsicAssetTag;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class TagAssetFilterTest {
    @Test
    public void testCustomTagWorks() {
        var selectedTagName = "selectedTag";
        var selectedTagValue = "selectedValue";

        var matchingAssetId1 = "matches1";
        var matchingAssetId2 = "matches2";

        var assets = Map.<String, SerAsset>of(
                matchingAssetId1, ImmSerAsset.of("Matches 1", AssetType.BANK_ACCOUNT).withCustomTags(Map.of(
                        selectedTagName, selectedTagValue
                )),
                "right-name-wrong-values", ImmSerAsset.of("Right name, wrong values", AssetType.BANK_ACCOUNT).withCustomTags(Map.of(
                        selectedTagName, "wrong-value"
                )),
                "unmatching-tags", ImmSerAsset.of("Unmatching tags", AssetType.BANK_ACCOUNT).withCustomTags(Map.of("foo", "bar")),
                "no-tags", ImmSerAsset.of("No tags", AssetType.BANK_ACCOUNT),
                matchingAssetId2, ImmSerAsset.of("Matches 2", AssetType.BANK_ACCOUNT).withCustomTags(Map.of(
                        selectedTagName, selectedTagValue
                ))
        );

        var customTags = Map.of(
                selectedTagName, ImmCustomTagDefinition.builder().build()
        );

        var filter = ImmTagAssetFilter.of(customTags, selectedTagName, selectedTagValue);
        var result = filter.apply(Map.of(), assets);
        var expected = Map.of(
                matchingAssetId1, assets.get(matchingAssetId1),
                matchingAssetId2, assets.get(matchingAssetId2)
        );
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
                AssetType.BANK_ACCOUNT.name()
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
