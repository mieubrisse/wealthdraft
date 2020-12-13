package com.strangegrotto.wealthdraft.networth;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.strangegrotto.wealthdraft.errors.ValOrGerr;
import com.strangegrotto.wealthdraft.networth.projections.AssetChange;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableBankAccountAssetSnapshot.class)
public interface BankAccountAssetSnapshot extends AssetSnapshot {
    int MONTHS_IN_YEAR = 12;

    long getBalance();

    Double getAnnualInterestRate();

    @Override
    @Value.Derived
    default Long getValue() {
        return getBalance();
    }

    @Override
    @Value.Derived
    default BankAccountAssetSnapshot projectOneMonth() {
        double monthlyMultiplier = Math.pow(1 + getAnnualInterestRate(), 1 / MONTHS_IN_YEAR);
        long futureBalance = (long)(getBalance() * monthlyMultiplier);
        return ImmutableBankAccountAssetSnapshot.builder()
                .balance(futureBalance)
                .annualInterestRate(getAnnualInterestRate())
                .build();
    }

    @Override
    default ValOrGerr<AssetSnapshot> applyChange(AssetChange change) {
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

        ValOrGerr<Long> newBalanceOrErr = castedChange.getBalance().apply(getBalance());
        if (newBalanceOrErr.hasGerr()) {
            return ValOrGerr.propGerr(
                    newBalanceOrErr.getGerr(),
                    "An error occurred applying the balance change");
        }

        // TODO Change interest rate!!

        // TODO fix up
        /*
        return ValOrGerr.val(ImmutableBankAccountAssetSnapshot.builder()
                .balance(newBalanceOrErr.getVal())
                .
                .build());

         */
        return null;
    }
}
