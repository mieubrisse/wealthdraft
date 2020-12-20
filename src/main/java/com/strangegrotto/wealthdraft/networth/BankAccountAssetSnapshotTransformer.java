package com.strangegrotto.wealthdraft.networth;

import com.strangegrotto.wealthdraft.errors.ValOrGerr;
import com.strangegrotto.wealthdraft.networth.projections.AssetSnapshotTransformer;

import java.math.BigDecimal;

public enum BankAccountAssetSnapshotTransformer implements AssetSnapshotTransformer<BankAccountAssetSnapshot, BankAccountAssetChange> {
    INSTANCE;

    int MONTHS_IN_YEAR = 12;

    @Override
    public BankAccountAssetSnapshot projectOneMonth(BankAccountAssetSnapshot snapshot) {
        // BigDecimal doesn't allow fractional exponents, so we drop down to Double (the loss of
        //  precision will be okay here)
        double balanceDouble = snapshot.getBalance().doubleValue();
        double annualInterestRateDouble = snapshot.getAnnualInterestRate().doubleValue();
        double exponent = 1.0 / (double) MONTHS_IN_YEAR;
        double monthlyMultiplier = Math.pow(1 + annualInterestRateDouble, exponent);
        double newBalanceDouble = balanceDouble * monthlyMultiplier;
        BigDecimal newBalance = BigDecimal.valueOf(newBalanceDouble);
        return ImmBankAccountAssetSnapshot.of(newBalance, snapshot.getAnnualInterestRate());
    }

    @Override
    public ValOrGerr<BankAccountAssetSnapshot> applyChange(BankAccountAssetSnapshot snapshot, BankAccountAssetChange change) {
        return change.apply(snapshot);
    }
}
