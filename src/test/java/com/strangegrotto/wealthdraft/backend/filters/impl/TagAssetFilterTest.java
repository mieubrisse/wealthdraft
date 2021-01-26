package com.strangegrotto.wealthdraft.backend.filters.impl;

import com.strangegrotto.wealthdraft.backend.assets.api.types.Asset;
import com.strangegrotto.wealthdraft.backend.assets.api.types.AssetType;
import com.strangegrotto.wealthdraft.backend.assets.api.types.MockAsset;
import com.strangegrotto.wealthdraft.backend.assets.impl.*;
import com.strangegrotto.wealthdraft.backend.tags.custom.api.types.CustomTagDefinition;
import com.strangegrotto.wealthdraft.backend.tags.custom.api.types.MockCustomTagDefinition;
import com.strangegrotto.wealthdraft.backend.tags.intrinsic.IntrinsicAssetTag;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class TagAssetFilterTest {
    @Test
    public void testCustomTagWorks() {
        var selectedTagName = "selectedTag";
        var selectedTagValue = "selectedValue";

        var matchingAssetId1 = "matches1";
        var matchingAssetId2 = "matches2";

        var assets = Map.<String, Asset>of(
                matchingAssetId1, new MockAsset("Matches 1", AssetType.BANK_ACCOUNT, Map.of(
                        selectedTagName, selectedTagValue
                )),
                "right-name-wrong-values", new MockAsset("Right name, wrong values", AssetType.BANK_ACCOUNT, Map.of(
                        selectedTagName, "wrong-value"
                )),
                "unmatching-tags", new MockAsset("Unmatching tags", AssetType.BANK_ACCOUNT, Map.of("foo", "bar")),
                "no-tags", new MockAsset("No tags", AssetType.BANK_ACCOUNT, Map.of()),
                matchingAssetId2, new MockAsset("Matches 2", AssetType.BANK_ACCOUNT, Map.of(
                        selectedTagName, selectedTagValue
                ))
        );

        var filter = ImmTagAssetFilter.of(selectedTagName, selectedTagValue);
        var result = filter.apply(Map.of(), assets);
        var expected = Map.of(
                matchingAssetId1, assets.get(matchingAssetId1),
                matchingAssetId2, assets.get(matchingAssetId2)
        );
        Assert.assertEquals(expected, result);
    }



    @Test
    public void testAssetTypeTagWorks() {
        var tagName = IntrinsicAssetTag.ASSET_TYPE.getTagName();
        var filter = ImmTagAssetFilter.of(
                tagName,
                AssetType.BANK_ACCOUNT.name()
        );
        filter.validate(Map.of(), Map.of());
    }

    @Test(expected = IllegalStateException.class)
    public void testErrorOnUnrecognizedCustomName() {
        var filter = ImmTagAssetFilter.of(
                "unrecognized-tag",
                "some-value"
        );
        filter.validate(Map.of(), Map.of());
    }

    @Test(expected = IllegalStateException.class)
    public void testErrorOnUrecognizedCustomValue() {
        var customTagName = "customTag";
        Map<String, CustomTagDefinition> customTags = Map.of(
                customTagName, new MockCustomTagDefinition(true, Set.of("value1", "value2"), Optional.empty())
        );
        var filter = ImmTagAssetFilter.of(
                customTagName,
                "some-nonexistent-value"
        );
        filter.validate(Map.of(), customTags);
    }

    @Test(expected = IllegalStateException.class)
    public void testErrorOnUrecognizedAssetType() {
        var tagName = IntrinsicAssetTag.ASSET_TYPE.getTagName();
        var filter = ImmTagAssetFilter.of(
                tagName,
                "some-nonexistent-value"
        );
        filter.validate(Map.of(), Map.of());
    }
}
