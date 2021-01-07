package com.strangegrotto.wealthdraft.assetallocation.calculator;

public enum AssetAllocationDeviationStatus {
    OK,     // Asset allocation deviation is within tolerances
    WARN,   // Asset allocation % has deviated somewhat
    ERROR,  // Asset allocation % has deviated significantly
}
