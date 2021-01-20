package com.strangegrotto.wealthdraft.backend.projections.impl;

import com.strangegrotto.wealthdraft.backend.projections.api.ProjectionsStore;
import com.strangegrotto.wealthdraft.backend.projections.api.types.ProjectionScenario;

import java.math.BigDecimal;
import java.util.Map;

class SimpleProjectionsStore implements ProjectionsStore {
    private final BigDecimal defaultAnnualGrowth;
    private final Map<String, ProjectionScenario> scenarios;

    public SimpleProjectionsStore(BigDecimal defaultAnnualGrowth, Map<String, ProjectionScenario> scenarios) {
        this.defaultAnnualGrowth = defaultAnnualGrowth;
        this.scenarios = Map.copyOf(scenarios);
    }

    @Override
    public BigDecimal getDefaultAnnualGrowth() {
        return this.defaultAnnualGrowth;
    }

    @Override
    public Map<String, ProjectionScenario> getScenarios() {
        return this.scenarios;
    }
}
