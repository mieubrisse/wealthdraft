package com.strangegrotto.wealthdraft.networth;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.annotations.VisibleForTesting;
import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import com.strangegrotto.wealthdraft.errors.ValOrGerr;
import com.strangegrotto.wealthdraft.networth.assets.AssetSnapshot;
import com.strangegrotto.wealthdraft.networth.projections.AssetChange;
import org.immutables.value.Value;

import java.math.BigDecimal;

@WealthdraftImmutableStyle
@Value.Immutable
@JsonDeserialize(as = ImmBankAccountAssetSnapshot.class)
public interface BankAccountAssetSnapshot extends AssetSnapshot {

    @VisibleForTesting
    BigDecimal getBalance();

    @VisibleForTesting
    BigDecimal getAnnualInterestRate();

    @Override
    @Value.Derived
    default BigDecimal getValue() {
        return getBalance();
    }
}
