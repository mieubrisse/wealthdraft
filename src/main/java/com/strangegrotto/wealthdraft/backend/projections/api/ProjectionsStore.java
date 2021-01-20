package com.strangegrotto.wealthdraft.backend.projections.api;

import com.strangegrotto.wealthdraft.backend.projections.api.types.ProjectionScenario;

import java.util.Map;

public interface ProjectionsStore {
    Map<String, ProjectionScenario> getScenarios();
}
