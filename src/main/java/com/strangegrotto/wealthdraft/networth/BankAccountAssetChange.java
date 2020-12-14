package com.strangegrotto.wealthdraft.networth;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import com.strangegrotto.wealthdraft.networth.assets.AssetType;
import com.strangegrotto.wealthdraft.networth.projections.AssetChange;
import com.strangegrotto.wealthdraft.networth.projections.AssetParameterChange;
import org.immutables.value.Value;

import java.util.Optional;

@WealthdraftImmutableStyle
@Value.Immutable
@JsonDeserialize(as = ImmBankAccountAssetChange.class)
public interface BankAccountAssetChange extends AssetChange {
    @Value.Derived
    default AssetType getApplicableType() {
        return AssetType.BANK_ACCOUNT;
    }

    Optional<AssetParameterChange> getBalance();

    Optional<AssetParameterChange> getAnnualInterestRate();
}
