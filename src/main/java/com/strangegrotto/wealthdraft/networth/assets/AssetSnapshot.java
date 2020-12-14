package com.strangegrotto.wealthdraft.networth.assets;

import com.strangegrotto.wealthdraft.errors.ValOrGerr;
import com.strangegrotto.wealthdraft.networth.projections.AssetChange;

import java.math.BigDecimal;

public interface AssetSnapshot {
    BigDecimal getValue();

    AssetSnapshot projectOneMonth();

    ValOrGerr<AssetSnapshot> applyChange(AssetChange change);
}
