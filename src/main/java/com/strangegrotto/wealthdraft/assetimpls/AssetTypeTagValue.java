package com.strangegrotto.wealthdraft.assetimpls;

/**
 * Huamn-readable values for the asset type, which will be the value for the asset type tag
 *
 * There should be one per custom asset implementation
 */
public enum AssetTypeTagValue {
    BANK_ACCOUNT("Bank Account");

    private final String value;

    AssetTypeTagValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
