package com.strangegrotto.wealthdraft.networth;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import com.strangegrotto.wealthdraft.errors.ValOrGerr;
import com.strangegrotto.wealthdraft.networth.assets.AssetType;
import com.strangegrotto.wealthdraft.networth.projections.AssetChange;
import com.strangegrotto.wealthdraft.networth.projections.AssetParameterChange;
import org.immutables.value.Value;

import java.math.BigDecimal;
import java.util.Optional;

@WealthdraftImmutableStyle
@Value.Immutable
@JsonDeserialize(as = ImmBankAccountAssetChange.class)
public interface BankAccountAssetChange extends AssetChange<BankAccountAssetSnapshot> {
    @Value.Derived
    default AssetType getApplicableType() {
        return AssetType.BANK_ACCOUNT;
    }

    Optional<AssetParameterChange> getBalance();

    Optional<AssetParameterChange> getAnnualInterestRate();

    @Override
    default ValOrGerr<BankAccountAssetSnapshot> apply(BankAccountAssetSnapshot snapshot) {
        var balanceModificationOpt = getBalance();
        var newBalance = snapshot.getBalance();
        if (balanceModificationOpt.isPresent()) {
            ValOrGerr<BigDecimal> newBalanceOrErr = balanceModificationOpt.get().apply(snapshot.getBalance());
            if (newBalanceOrErr.hasGerr()) {
                return ValOrGerr.propGerr(
                        newBalanceOrErr.getGerr(),
                        "An error occurred applying the balance change");
            }
            newBalance = newBalanceOrErr.getVal();
        }

        var interestRateModificationOpt = getAnnualInterestRate();
        var newInterestRate = snapshot.getAnnualInterestRate();
        if (interestRateModificationOpt.isPresent()) {
            ValOrGerr<BigDecimal> newInterestRateOrErr = interestRateModificationOpt.get().apply(snapshot.getAnnualInterestRate());
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
