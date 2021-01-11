package com.strangegrotto.wealthdraft.projections.impl;

import com.strangegrotto.wealthdraft.projections.api.ProjectionsStore;
import com.strangegrotto.wealthdraft.projections.api.types.ProjectionScenario;

import java.util.Map;

public class SimpleProjectionsStore implements ProjectionsStore {
    private final Map<String, ProjectionScenario> scenarios;

    public SimpleProjectionsStore(Map<String, ProjectionScenario> scenarios) {
        this.scenarios = Map.copyOf(scenarios);
    }

    @Override
    public Map<String, SerProjectionScenario> getScenarios() {
        return this.scenarios;
    }
}
