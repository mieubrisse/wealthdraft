package com.strangegrotto.wealthdraft.assets.temporal;

import com.strangegrotto.wealthdraft.errors.ValOrGerr;

import java.math.BigDecimal;

public interface AssetSnapshot<CHANGE extends AssetChange> {
    BigDecimal getValue();

    // TODO Make this get an AssetChange instead, and feed into the regular applyChange machinery
    AssetSnapshot<CHANGE> projectOneMonth();

    ValOrGerr<AssetSnapshot<CHANGE>> applyChange(AssetChange change) throws ClassCastException;
}
