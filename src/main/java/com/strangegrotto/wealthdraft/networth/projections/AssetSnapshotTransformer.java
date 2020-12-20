package com.strangegrotto.wealthdraft.networth.projections;

import com.strangegrotto.wealthdraft.errors.ValOrGerr;
import com.strangegrotto.wealthdraft.networth.assets.AssetSnapshot;

/**
 * Interface representing logic for transforming a snapshot to get another snapshot
 * @param <S> Snapshot type
 * @param <C> Change type
 */
public interface AssetSnapshotTransformer<S extends AssetSnapshot, C extends AssetChange<S>> {

    S projectOneMonth(S snapshot);

    ValOrGerr<S> applyChange(S snapshot, C change);
}
