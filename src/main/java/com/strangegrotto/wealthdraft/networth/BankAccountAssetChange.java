package com.strangegrotto.wealthdraft.networth;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.strangegrotto.wealthdraft.networth.projections.AssetChange;
import com.strangegrotto.wealthdraft.networth.projections.AssetParameterChange;
import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
@JsonDeserialize(as = ImmutableBankAccountAssetChange.class)
public interface BankAccountAssetChange extends AssetChange {
    default AssetType getApplicableType() {
        return AssetType.BANK_ACCOUNT;
    }

    Optional<AssetParameterChange> getBalance();

    Optional<AssetParameterChange> getAnnualInterestRate();
}
