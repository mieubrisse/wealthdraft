package com.strangegrotto.wealthdraft.assets.temporal;

import com.strangegrotto.wealthdraft.errors.ValOrGerr;

import java.math.BigDecimal;

public interface AssetSnapshot<CHANGE extends AssetChange> {
    BigDecimal getValue();

    AssetSnapshot<CHANGE> projectOneMonth();

    ValOrGerr<AssetSnapshot<CHANGE>> applyChange(CHANGE change);
}
