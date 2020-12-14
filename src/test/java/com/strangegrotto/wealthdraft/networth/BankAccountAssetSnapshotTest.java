package com.strangegrotto.wealthdraft.networth;

import com.strangegrotto.wealthdraft.networth.projections.AssetParameterChangeValueOperation;
import com.strangegrotto.wealthdraft.networth.projections.ImmutableAssetParameterChange;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class BankAccountAssetSnapshotTest {
    @Test
    public void testProjectionMath() {
        var expectedResult = BigDecimal.valueOf(100.25);
        var snapshot = new BankAccountAssetSnapshot(new BigDecimal(100), BigDecimal.valueOf(0.03));
        var newSnapshot = snapshot.projectOneMonth();
        var roundedNewBalance = newSnapshot.getValue().setScale(2, RoundingMode.HALF_EVEN);
        Assert.assertEquals(
                expectedResult,
                roundedNewBalance
        );
    }

    @Test
    public void testChangeApplication() {
        BigDecimal newBalance = new BigDecimal(50);
        BigDecimal newInterestRate = new BigDecimal(0.05);

        var snapshot = new BankAccountAssetSnapshot(new BigDecimal(100), new BigDecimal(0.03));
        var balanceChangeOperation = ImmutableAssetParameterChange.builder()
                .operation(AssetParameterChangeValueOperation.SET)
                .value(newBalance)
                .build();
        var interestRateChangeOperation = ImmutableAssetParameterChange.builder()
                .operation(AssetParameterChangeValueOperation.SET)
                .value(newInterestRate)
                .build();
        var change = ImmutableBankAccountAssetChange.builder()
                .balance(balanceChangeOperation)
                .annualInterestRate(interestRateChangeOperation)
                .build();

        var newSnapshotOrErr = snapshot.applyChange(change);
        Assert.assertFalse(newSnapshotOrErr.hasGerr());
        // TODO Remove this nasty cast by generic-typing!!
        BankAccountAssetSnapshot castedNewSnapshot = (BankAccountAssetSnapshot) newSnapshotOrErr.getVal();
        Assert.assertEquals(newBalance, castedNewSnapshot.balance);
        Assert.assertEquals(newInterestRate, castedNewSnapshot.annualInterestRate);
    }
}
