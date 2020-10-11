package com.strangegrotto.wealthdraft.govconstants.objects;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableRetirementConstants.class)
public interface RetirementConstants {
    int getPersonal401kContribLimit();
    int getTotal401kContribLimit();
    int getIraContribLimit();
    int getTradIraDeductiblePhaseoutFloor();
    int getTradIraDeductiblePhaseoutCeiling();

    @Value.Check
    default void check() {
        Preconditions.checkState(getPersonal401kContribLimit() > 0, "Personal 401k contribution limit must be > 0");
        Preconditions.checkState(
                getTotal401kContribLimit() > getPersonal401kContribLimit(),
                "Total 401k contribution limit must be > personal 401k contribution limit"
        );
        Preconditions.checkState(getIraContribLimit() > 0, "IRA contribution limit must be > 0");
        Preconditions.checkState(getTradIraDeductiblePhaseoutFloor() > 0, "Floor where traditional IRA is deductible must be > 0");
        Preconditions.checkState(
                getTradIraDeductiblePhaseoutCeiling() > getTradIraDeductiblePhaseoutFloor(),
                "Ceiling where traditional IRA is no longer deductible must be > floor where traditional IRA deduction starts phasing out"
        );
    }
}
