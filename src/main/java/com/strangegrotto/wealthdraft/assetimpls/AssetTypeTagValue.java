package com.strangegrotto.wealthdraft.assetimpls;

/**
 * Huamn-readable values for the asset type, which will be the value for the asset type tag
 *
 * There should be one per custom asset implementation
 */
public enum AssetTypeTagValue {
    // TODO Replace this entire enum with an AssetType enum, and use that enum for the deserialization of Assets
    //  in AssetDefinitionsDeserializer
    BANK_ACCOUNT,
    STOCK;
}
