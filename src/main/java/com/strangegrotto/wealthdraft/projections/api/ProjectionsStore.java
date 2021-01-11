package com.strangegrotto.wealthdraft.projections.api;

import com.strangegrotto.wealthdraft.errors.ValOrGerr;
import com.strangegrotto.wealthdraft.projections.api.types.ProjectionScenario;
import com.strangegrotto.wealthdraft.projections.impl.SerProjectionScenario;

import java.util.Map;

public interface ProjectionsStore {
    Map<String, ProjectionScenario> getScenarios();
}
