package com.strangegrotto.wealthdraft.assetimpls.bankaccount;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import com.strangegrotto.wealthdraft.projections.impl.temporal.AssetChange;
import com.strangegrotto.wealthdraft.projections.impl.temporal.AssetParameterChange;
import org.immutables.value.Value;

import java.util.Optional;

@WealthdraftImmutableStyle
@Value.Immutable
@JsonDeserialize(as = ImmBankAccountAssetChange.class)
public interface BankAccountAssetChange extends AssetChange {
    @Value.Parameter
    @JsonProperty("balance")
    Optional<AssetParameterChange> getBalance();

    @Value.Parameter
    @JsonProperty("annualInterestRate")
    Optional<AssetParameterChange> getAnnualInterestRate();
}
