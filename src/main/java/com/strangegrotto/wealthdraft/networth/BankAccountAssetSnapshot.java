package com.strangegrotto.wealthdraft.networth;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
@JsonDeserialize(as = ImmutableBankAccountAssetSnapshot.class)
public interface BankAccountAssetSnapshot extends AssetSnapshot {
    long getBalance();

    Optional<Double> getAnnualInterestRate();

    @Override
    @Value.Derived
    default Long getValue() {
        return getBalance();
    }
}
