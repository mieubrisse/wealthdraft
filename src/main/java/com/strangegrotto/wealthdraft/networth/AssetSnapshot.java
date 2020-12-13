package com.strangegrotto.wealthdraft.networth;

import com.strangegrotto.wealthdraft.errors.ValOrGerr;
import com.strangegrotto.wealthdraft.networth.projections.AssetChange;

public interface AssetSnapshot {
    AssetType getType();

    Long getValue();

    AssetSnapshot projectOneMonth();

    ValOrGerr<AssetSnapshot> applyChange(AssetChange change);
}
