package com.strangegrotto.wealthdraft.assetimpls.bankaccount;

import com.strangegrotto.wealthdraft.backend.projections.impl.temporal.AssetParameterChangeValueOperation;
import com.strangegrotto.wealthdraft.backend.projections.impl.temporal.ImmSerAssetParameterChange;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BankAccountAssetSnapshotTest {
    @Test
    public void testProjectionMath() {
        var expectedResult = BigDecimal.valueOf(100.25);
        var snapshot = ImmBankAccountAssetSnapshot.of(new BigDecimal(100), BigDecimal.valueOf(0.03));
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
        BigDecimal newInterestRate = BigDecimal.valueOf(0.05);

        var snapshot = ImmBankAccountAssetSnapshot.of(new BigDecimal(100), BigDecimal.valueOf(0.03));
        var balanceChangeOperation = ImmSerAssetParameterChange.of(newBalance,AssetParameterChangeValueOperation.SET);
        var interestRateChangeOperation = ImmSerAssetParameterChange.of(newInterestRate, AssetParameterChangeValueOperation.SET);
        var change = ImmBankAccountAssetChange.builder()
                .balance(balanceChangeOperation)
                .annualInterestRate(interestRateChangeOperation)
                .build();

        var newSnapshotOrErr = snapshot.applyChange(change);
        Assert.assertFalse(newSnapshotOrErr.hasGerr());
        // TODO Remove this nasty cast by generic-typing!!
        BankAccountAssetSnapshot castedNewSnapshot = (BankAccountAssetSnapshot) newSnapshotOrErr.getVal();
        Assert.assertEquals(newBalance, castedNewSnapshot.getBalance());
        Assert.assertEquals(newInterestRate, castedNewSnapshot.getAnnualInterestRate());
    }
}
