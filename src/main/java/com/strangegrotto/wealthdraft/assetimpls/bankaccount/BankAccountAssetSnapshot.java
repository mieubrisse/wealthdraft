package com.strangegrotto.wealthdraft.assetimpls.bankaccount;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.annotations.VisibleForTesting;
import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import com.strangegrotto.wealthdraft.errors.ValOrGerr;
import com.strangegrotto.wealthdraft.assets.temporal.AssetSnapshot;
import com.strangegrotto.wealthdraft.assets.temporal.AssetChange;
import org.immutables.value.Value;

import java.math.BigDecimal;

public class BankAccountAssetSnapshot implements AssetSnapshot {
    private static final int MONTHS_IN_YEAR = 12;

    private final BigDecimal balance;
    private final BigDecimal annualInterestRate;

    @JsonCreator
    public BankAccountAssetSnapshot(
            @JsonProperty("balance") BigDecimal balance,
            @JsonProperty("annualInterestRate") BigDecimal annualInterestRate
    ) {
        this.balance = balance;
        this.annualInterestRate = annualInterestRate;
    }

    @VisibleForTesting
    BigDecimal getBalance() {
        return this.balance;
    }

    @VisibleForTesting
    BigDecimal getAnnualInterestRate() {
        return this.annualInterestRate;
    }

    @Override
    public BigDecimal getValue() {
        return getBalance();
    }


    @Override
    public BankAccountAssetSnapshot projectOneMonth() {
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
    public ValOrGerr<AssetSnapshot> applyChange(AssetChange change) {
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

        var newSnapshot = ImmBankAccountAssetSnapshot.of(newBalance, newInterestRate);
        return ValOrGerr.val(newSnapshot);
    }
}
