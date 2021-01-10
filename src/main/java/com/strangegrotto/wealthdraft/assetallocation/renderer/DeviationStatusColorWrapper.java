package com.strangegrotto.wealthdraft.assetallocation.renderer;

import com.google.common.base.Strings;
import com.strangegrotto.wealthdraft.assetallocation.calculator.AssetAllocationDeviationStatus;

class DeviationStatusColorWrapper {
    private static final String ANSI_WHITE = "\u001B[37m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_RESET = "\u001B[0m";

    private final AssetAllocationDeviationStatus deviationStatus;

    public DeviationStatusColorWrapper(AssetAllocationDeviationStatus deviationStatus) {
        this.deviationStatus = deviationStatus;
    }

    String wrap(String input) {
        String colorStr;
        switch (this.deviationStatus) {
            case ERROR:
                colorStr = ANSI_RED;
                break;
            case WARN:
                colorStr = ANSI_YELLOW;
                break;
            case OK:
                colorStr = ANSI_WHITE;
                break;
            default:
                throw new RuntimeException(Strings.lenientFormat(
                        "Unrecognized deviation status '%s'; this is a code bug",
                        this.deviationStatus
                ));
        }
        return colorStr + input + ANSI_RESET;
    }
}
