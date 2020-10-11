package com.strangegrotto.wealthdraft.scenarios;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import com.strangegrotto.wealthdraft.validator.DeserializationValidator;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@JsonDeserialize(as = ImmutableScenario.class)
public interface Scenario {
    @JsonProperty("401kContrib") TradOrRothContribs get401kContrib();

    TradOrRothContribs getIraContrib();

    List<Long> getEarnedIncome();

    // What fraction of my earned income was from foreign sources
    double getFractionForeignEarnedIncome();

    List<Long> getLongTermCapitalGains();

    List<Long> getShortTermCapitalGains();

    @Value.Check
    default void check() {
        DeserializationValidator.checkIsRatio("fraction foreign earned income", getFractionForeignEarnedIncome());
        for (Long earnedIncomePart : getEarnedIncome()) {
            Preconditions.checkState(
                    earnedIncomePart >= 0,
                    "All earned income entries must be >= 0, but found earned entry '%s'");
        }
    }
}
