package com.strangegrotto.wealthdraft.networth;

import com.strangegrotto.wealthdraft.networth.projections.AssetParameterChangeValueOperation;
import com.strangegrotto.wealthdraft.networth.projections.ImmutableAssetParameterChange;
import org.junit.Assert;
import org.junit.Test;

public class BankAccountAssetSnapshotTest {
    @Test
    public void testProjectionMath() {
        var snapshot = ImmutableBankAccountAssetSnapshot.builder()
                .balance(100)
                .annualInterestRate(0.03)
                .build();
        var newSnapshot = snapshot.projectOneMonth();
        Assert.assertEquals(142L, newSnapshot.getValue().longValue());
    }

    @Test
    public void testChangeApplication() {
        long newBalance = 50;
        var snapshot = ImmutableBankAccountAssetSnapshot.builder()
                .balance(100)
                .annualInterestRate(0.03)
                .build();
        var balanceChangeOperation = ImmutableAssetParameterChange.builder()
                .operation(AssetParameterChangeValueOperation.SET)
                .value(newBalance)
                .build();
        var change = ImmutableBankAccountAssetChange.builder()
                .balance(balanceChangeOperation)
                .build();

        var newSnapshotOrErr = snapshot.applyChange(change);
        Assert.assertFalse(newSnapshotOrErr.hasGerr());
        // TODO Remove this nasty cast by generic-typing!!
        BankAccountAssetSnapshot castedNewSnapshot = (BankAccountAssetSnapshot) newSnapshotOrErr.getVal();
        Assert.assertEquals(newBalance, castedNewSnapshot.getBalance());
        // TODO Add interest rate changing and test that here too!!
    }
}
