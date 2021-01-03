package com.strangegrotto.wealthdraft.assetimpls.stock;

import com.strangegrotto.wealthdraft.assets.temporal.AssetParameterChangeValueOperation;
import com.strangegrotto.wealthdraft.assets.temporal.ImmAssetParameterChange;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Map;

// TODO Maybe make this an interface????
public class StockAssetSnapshotTest {
    @Test
    public void testProjectionMath() {
        var expectedSnapshotValue = BigDecimal.valueOf(60);
        var snapshot = ImmStockAssetSnapshot.of(BigDecimal.valueOf(12), BigDecimal.valueOf(5));
        var snapshotValue = snapshot.getValue();
        Assert.assertEquals(expectedSnapshotValue, snapshotValue);

        var expectedFutureSnapshotValue = BigDecimal.valueOf(60);
        var futureSnapshot = snapshot.projectOneMonth();
        var futureSnapshotValue = futureSnapshot.getValue();
        Assert.assertEquals(expectedFutureSnapshotValue, futureSnapshotValue);
    }

    @Test
    public void testChangeApplication() {
        var snapshot = ImmStockAssetSnapshot.of(BigDecimal.valueOf(10), BigDecimal.valueOf(10));
        var change = ImmStockAssetChange.of(
                ImmAssetParameterChange.of(
                        BigDecimal.valueOf(5),
                        AssetParameterChangeValueOperation.ADD
                ),
                ImmAssetParameterChange.of(
                        BigDecimal.valueOf(2),
                        AssetParameterChangeValueOperation.SUBTRACT
                )
        );

        var newSnapshot = snapshot.applyChange(change);
        Assert.assertFalse(newSnapshot.hasGerr());

        var newValue = newSnapshot.getVal().getValue();
        var expectedNewValue = BigDecimal.valueOf(120);
        Assert.assertEquals(expectedNewValue, newValue);
    }
}
