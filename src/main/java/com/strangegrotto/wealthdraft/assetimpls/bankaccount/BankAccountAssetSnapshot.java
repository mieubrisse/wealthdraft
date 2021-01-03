package com.strangegrotto.wealthdraft.assetimpls.bankaccount;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.annotations.VisibleForTesting;
import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import com.strangegrotto.wealthdraft.errors.ValOrGerr;
import com.strangegrotto.wealthdraft.assets.temporal.AssetSnapshot;
import com.strangegrotto.wealthdraft.assets.temporal.AssetChange;
import org.immutables.value.Value;

import java.math.BigDecimal;

@WealthdraftImmutableStyle
@Value.Immutable
@JsonDeserialize(as = ImmBankAccountAssetSnapshot.class)
public abstract class BankAccountAssetSnapshot implements AssetSnapshot<BankAccountAssetChange> {
    private static final int MONTHS_IN_YEAR = 12;

    // ================================================================================
    //               Logic custom this class, not filled by Immutables
    // ================================================================================
    @Override
    public final BankAccountAssetSnapshot projectOneMonth() {
        // BigDecimal doesn't allow fractional exponents, so we drop down to Double (the loss of
        //  precision will be okay here)
        double balanceDouble = getBalance().doubleValue();
        double annualInterestRateDouble = getAnnualInterestRate().doubleValue();
        double exponent = 1.0 / (double) MONTHS_IN_YEAR;
        double monthlyMultiplier = Math.pow(1 + annualInterestRateDouble, exponent);
        double newBalanceDouble = balanceDouble * monthlyMultiplier;
        BigDecimal newBalance = BigDecimal.valueOf(newBalanceDouble);
        return ImmBankAccountAssetSnapshot.of(newBalance, getAnnualInterestRate());
    }

    @Override
    public final ValOrGerr<AssetSnapshot> applyChange(BankAccountAssetChange change) {
        var balanceModificationOpt = change.getBalance();
        var newBalance = getBalance();
        if (balanceModificationOpt.isPresent()) {
            ValOrGerr<BigDecimal> newBalanceOrErr = balanceModificationOpt.get().apply(getBalance());
            if (newBalanceOrErr.hasGerr()) {
                return ValOrGerr.propGerr(
                        newBalanceOrErr.getGerr(),
                        "An error occurred applying the balance change");
            }
            newBalance = newBalanceOrErr.getVal();
        }

        var interestRateModificationOpt = change.getAnnualInterestRate();
        var newInterestRate = getAnnualInterestRate();
        if (interestRateModificationOpt.isPresent()) {
            ValOrGerr<BigDecimal> newInterestRateOrErr = interestRateModificationOpt.get().apply(getAnnualInterestRate());
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

    // ================================================================================
    //                     Functions filled by Immutables
    // ================================================================================
    @VisibleForTesting
    abstract BigDecimal getBalance();

    @VisibleForTesting
    abstract BigDecimal getAnnualInterestRate();

    @Override
    @Value.Derived
    public BigDecimal getValue() {
        return getBalance();
    }
}
