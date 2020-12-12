package com.strangegrotto.wealthdraft.networth;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.strangegrotto.wealthdraft.errors.ValOrGerr;
import com.strangegrotto.wealthdraft.networth.projections.AssetChange;
import com.strangegrotto.wealthdraft.networth.projections.AssetParameterChange;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableBankAccountAssetChange.class)
public interface BankAccountAssetChange extends AssetChange<BankAccountAssetSnapshot> {

    AssetParameterChange getBalance();

    @Override
    @Value.Derived
    default ValOrGerr<BankAccountAssetSnapshot> apply(BankAccountAssetSnapshot snapshot) {
        ValOrGerr<Long> newBalanceOrErr = getBalance().apply(snapshot.getBalance());
        if (newBalanceOrErr.hasGerr()) {
            return ValOrGerr.propGerr(
                    newBalanceOrErr.getGerr(),
                    "An error occurred applying the balance change");
        }
        return ValOrGerr.val(ImmutableBankAccountAssetSnapshot.builder()
                .balance(newBalanceOrErr.getVal())
                .build());
    }
}
