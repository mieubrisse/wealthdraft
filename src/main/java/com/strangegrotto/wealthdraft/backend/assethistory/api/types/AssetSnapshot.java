package com.strangegrotto.wealthdraft.backend.assethistory.api.types;

import com.strangegrotto.wealthdraft.backend.assethistory.impl.AbstractAssetSnapshot;
import com.strangegrotto.wealthdraft.backend.projections.api.types.AssetChange;
import com.strangegrotto.wealthdraft.errors.ValOrGerr;

import java.math.BigDecimal;

/**
 * Do not implement this interface directly - instead, inherit from {@link AbstractAssetSnapshot}
 * @param <CHANGE> The type of change class that this snapshot will consume/produce
 */
public interface AssetSnapshot<CHANGE extends AssetChange> {
    BigDecimal getValue();

    // TODO Make this get an AssetChange instead, and feed into the regular applyChange machinery
    AssetSnapshot<CHANGE> projectOneMonth();

    ValOrGerr<AssetSnapshot<CHANGE>> applyChange(AssetChange change) throws ClassCastException;
}
