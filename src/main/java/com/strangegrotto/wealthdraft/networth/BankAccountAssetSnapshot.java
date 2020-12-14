package com.strangegrotto.wealthdraft.networth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.annotations.VisibleForTesting;
import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import com.strangegrotto.wealthdraft.errors.ValOrGerr;
import com.strangegrotto.wealthdraft.networth.projections.AssetChange;
import org.immutables.value.Value;

import java.math.BigDecimal;

@WealthdraftImmutableStyle
@Value.Immutable
@JsonDeserialize(as = ImmBankAccountAssetSnapshot.class)
public interface BankAccountAssetSnapshot extends AssetSnapshot {
    int MONTHS_IN_YEAR = 12;

    @VisibleForTesting
    BigDecimal getBalance();

    @VisibleForTesting
    BigDecimal getAnnualInterestRate();

    @Override
    @Value.Derived
    default BigDecimal getValue() {
        return getBalance();
    }


    @Override
    default BankAccountAssetSnapshot projectOneMonth() {
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
    default ValOrGerr<AssetSnapshot> applyChange(AssetChange change) {
        BankAccountAssetChange castedChange = (BankAccountAssetChange)change;

        var balanceModificationOpt = castedChange.getBalance();
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

        var interestRateModificationOpt = castedChange.getAnnualInterestRate();
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
}
