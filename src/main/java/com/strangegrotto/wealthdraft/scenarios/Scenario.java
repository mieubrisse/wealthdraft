package com.strangegrotto.wealthdraft.scenarios;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.math.BigDecimal;
import java.util.List;

@Value.Immutable
@JsonDeserialize(as = ImmutableScenario.class)
public interface Scenario {
    @JsonProperty("401kContrib") BigDecimal get401kContrib();

    BigDecimal getIraContrib();

    List<BigDecimal> getEarnedIncome();

    // What fraction of my earned income was from foreign sources
    Double getFractionForeignEarnedIncome();

    List<BigDecimal> getLongTermCapitalGains();

    List<BigDecimal> getShortTermCapitalGains();
}
