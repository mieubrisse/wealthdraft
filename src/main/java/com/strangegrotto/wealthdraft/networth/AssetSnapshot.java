package com.strangegrotto.wealthdraft.networth;

import com.strangegrotto.wealthdraft.networth.projections.AssetChange;

public interface AssetSnapshot {
    AssetType getType();

    Long getValue();

    AssetSnapshot projectOneMonth();

    AssetSnapshot applyChange(AssetChange<? extends AssetSnapshot> change) throws ClassCastException;
}
