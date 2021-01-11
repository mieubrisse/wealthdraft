package com.strangegrotto.wealthdraft.projections.api;

import com.strangegrotto.wealthdraft.projections.impl.SerProjectionScenario;

import java.util.Map;

public interface ProjectionsStore {
    Map<String, SerProjectionScenario> getScenarios();
}
