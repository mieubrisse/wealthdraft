package com.strangegrotto.wealthdraft.assetimpls.bankaccount;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import com.strangegrotto.wealthdraft.backend.projections.api.types.AssetChange;
import com.strangegrotto.wealthdraft.backend.projections.impl.temporal.SerAssetParameterChange;
import org.immutables.value.Value;

import java.util.Optional;

@WealthdraftImmutableStyle
@Value.Immutable
@JsonDeserialize(as = ImmBankAccountAssetChange.class)
public interface BankAccountAssetChange extends AssetChange {
    @Value.Parameter
    @JsonProperty("balance")
    Optional<SerAssetParameterChange> getBalance();

    @Value.Parameter
    @JsonProperty("annualInterestRate")
    Optional<SerAssetParameterChange> getAnnualInterestRate();
}
