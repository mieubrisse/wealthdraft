package com.strangegrotto.wealthdraft.scenarios;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import com.strangegrotto.wealthdraft.validator.DeserializationValidator;
import org.immutables.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Function;

// TODO Rename this to clarify that this is particularly around tax...
//  ...OR fuse this with the asset scenario calculator that's getting built
@Value.Immutable
@JsonDeserialize(as = ImmutableTaxScenario.class)
public interface TaxScenario {
    Logger log = LoggerFactory.getLogger(TaxScenario.class);

    int getYear();

    @JsonProperty("401kContrib")
    TradOrRothContribs get401kContrib();

    TradOrRothContribs getIraContrib();

    HsaContrib getHsaContrib();

    List<Long> getEarnedIncome();

    // What fraction of my earned income was from foreign sources
    double getFractionForeignEarnedIncome();

    List<Long> getLongTermCapitalGains();

    List<Long> getShortTermCapitalGains();

    List<Long> getOrdinaryDividends();

    List<Long> getQualifiedDividends();

    // TODO Replace with a set of more descriptive properties
    List<Long> getOtherUnearnedIncome();

    // TODO There are a whoooole bunch of extra AMT adjustments that need to be added back here
    //  Right now this relies on the user to know this, but ideally this should be handled in the YAML
    List<Long> getAmtAdjustments();

    List<Long> getTaxAlreadyPaid();

    @Value.Check
    default void check() {
        // TODO In the check, make sure that any List element has >= 1 element, because
        //  Jackson sadly won't fail with an error if a key corresponding to a List isn't specified -
        //  it'll just create an empty list

        DeserializationValidator.checkIsRatio("fraction foreign earned income", getFractionForeignEarnedIncome());
        for (Long earnedIncomePart : getEarnedIncome()) {
            Preconditions.checkState(
                    earnedIncomePart >= 0,
                    "All earned income entries must be >= 0, but found earned entry '%s'");
        }
    }

    @Value.Derived
    default IncomeStreams getIncomeStreams() {
        Function<List<Long>, Long> aggregateIncome = list -> list.stream().reduce(
                0L,
                (l, r) -> l + r
        );

        log.debug("Ordinary dividends: {}", getOrdinaryDividends());

        long earnedIncome = aggregateIncome.apply(getEarnedIncome());
        long ltcg = aggregateIncome.apply(getLongTermCapitalGains());
        long stcg = aggregateIncome.apply(getShortTermCapitalGains());
        long otherUnearnedIncome = aggregateIncome.apply(getOtherUnearnedIncome());
        long ordinaryDividends = aggregateIncome.apply(getOrdinaryDividends());
        long qualifiedDividends = aggregateIncome.apply(getQualifiedDividends());

        long nonPrefUnearnedIncome = stcg +
                ordinaryDividends +
                otherUnearnedIncome;

        long prefUnearnedIncome = ltcg +
                qualifiedDividends;

        return ImmutableIncomeStreams.builder()
                .earnedIncome(earnedIncome)
                .nonPreferentialUnearnedIncome(nonPrefUnearnedIncome)
                .preferentialUnearnedIncome(prefUnearnedIncome)
                .build();
    }
}
