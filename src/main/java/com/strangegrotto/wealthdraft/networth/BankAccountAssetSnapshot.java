package com.strangegrotto.wealthdraft.networth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.annotations.VisibleForTesting;
import com.strangegrotto.wealthdraft.errors.ValOrGerr;
import com.strangegrotto.wealthdraft.networth.projections.AssetChange;

import java.math.BigDecimal;

public final class BankAccountAssetSnapshot extends AssetSnapshot {
    private static final int MONTHS_IN_YEAR = 12;

    @VisibleForTesting
    final BigDecimal balance;

    @VisibleForTesting
    final BigDecimal annualInterestRate;

    @JsonCreator
    public BankAccountAssetSnapshot(
            @JsonProperty("balance") BigDecimal balance,
            @JsonProperty("annualInterestRate") BigDecimal annualInterestRate) {
        this.balance = balance;
        this.annualInterestRate = annualInterestRate;
    }

    @Override
    protected final AssetType getType() {
        return AssetType.BANK_ACCOUNT;
    }

    @Override
    public BigDecimal getValue() {
        return balance;
    }

    @Override
    public final BankAccountAssetSnapshot projectOneMonth() {
        // BigDecimal doesn't allow fractional exponents, so we drop down to Double (the loss of
        //  precision will be okay here)
        double balanceDouble = this.balance.doubleValue();
        double annualInterestRateDouble = this.annualInterestRate.doubleValue();
        double exponent = 1.0 / (double) MONTHS_IN_YEAR;
        double monthlyMultiplier = Math.pow(1 + annualInterestRateDouble, exponent);
        double newBalanceDouble = balanceDouble * monthlyMultiplier;
        BigDecimal newBalance = BigDecimal.valueOf(newBalanceDouble);
        return new BankAccountAssetSnapshot(newBalance, this.annualInterestRate);
    }

    @Override
    protected final ValOrGerr<AssetSnapshot> applyChangeInner(AssetChange change) {
        AssetType snapshotType = getType();
        AssetType changeApplicableType = change.getApplicableType();
        if (!snapshotType.equals(changeApplicableType)) {
            return ValOrGerr.newGerr(
                    "Snapshot is of type {} but change is applicable to snapshots of type {}",
                    snapshotType,
                    changeApplicableType
            );
        }
        BankAccountAssetChange castedChange = (BankAccountAssetChange)change;

        var balanceModificationOpt = castedChange.getBalance();
        var newBalance = this.balance;
        if (balanceModificationOpt.isPresent()) {
            ValOrGerr<BigDecimal> newBalanceOrErr = balanceModificationOpt.get().apply(this.balance);
            if (newBalanceOrErr.hasGerr()) {
                return ValOrGerr.propGerr(
                        newBalanceOrErr.getGerr(),
                        "An error occurred applying the balance change");
            }
            newBalance = newBalanceOrErr.getVal();
        }

        var interestRateModificationOpt = castedChange.getAnnualInterestRate();
        var newInterestRate = this.annualInterestRate;
        if (interestRateModificationOpt.isPresent()) {
            ValOrGerr<BigDecimal> newInterestRateOrErr = interestRateModificationOpt.get().apply(this.annualInterestRate);
            if (newInterestRateOrErr.hasGerr()) {
                return ValOrGerr.propGerr(
                        newInterestRateOrErr.getGerr(),
                        "An error occurred applying the annual interest rate change");
            }
            newInterestRate = newInterestRateOrErr.getVal();
        }


        return ValOrGerr.val(new BankAccountAssetSnapshot(newBalance, newInterestRate));
    }
}
