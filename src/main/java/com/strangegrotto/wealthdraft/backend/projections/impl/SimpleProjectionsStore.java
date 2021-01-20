package com.strangegrotto.wealthdraft.backend.projections.impl;

import com.strangegrotto.wealthdraft.backend.projections.api.ProjectionsStore;
import com.strangegrotto.wealthdraft.backend.projections.api.types.ProjectionScenario;

import java.util.Map;

class SimpleProjectionsStore implements ProjectionsStore {
    private final Map<String, ProjectionScenario> scenarios;

    public SimpleProjectionsStore(Map<String, ProjectionScenario> scenarios) {
        this.scenarios = Map.copyOf(scenarios);
    }

    @Override
    public Map<String, ProjectionScenario> getScenarios() {
        return this.scenarios;
    }
}
