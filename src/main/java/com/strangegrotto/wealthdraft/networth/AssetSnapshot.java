package com.strangegrotto.wealthdraft.networth;

import com.strangegrotto.wealthdraft.errors.ValOrGerr;
import com.strangegrotto.wealthdraft.networth.projections.AssetChange;

import java.math.BigDecimal;

public abstract class AssetSnapshot {
    protected abstract AssetType getType();

    public abstract BigDecimal getValue();

    public abstract AssetSnapshot projectOneMonth();

    // TODO Maybe push this check into NetWorthRenderer, where we actually do the change application????
    public final ValOrGerr<AssetSnapshot> applyChange(AssetChange change) {
        AssetType snapshotType = getType();
        AssetType changeApplicableType = change.getApplicableType();
        if (!snapshotType.equals(changeApplicableType)) {
            return ValOrGerr.newGerr(
                    "Snapshot is of type {} but change is only applicable to snapshots of type {}",
                    snapshotType,
                    changeApplicableType
            );
        }

        return applyChangeInner(change);
    }

    protected abstract ValOrGerr<AssetSnapshot> applyChangeInner(AssetChange change);
}
