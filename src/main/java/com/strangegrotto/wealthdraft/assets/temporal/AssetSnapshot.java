package com.strangegrotto.wealthdraft.assets.temporal;

import com.strangegrotto.wealthdraft.errors.ValOrGerr;

import java.math.BigDecimal;

public interface AssetSnapshot {
    BigDecimal getValue();

    AssetSnapshot projectOneMonth();

    ValOrGerr<AssetSnapshot> applyChange(AssetChange change);
}
