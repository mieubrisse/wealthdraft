package com.strangegrotto.wealthdraft.networth.assets;

import com.strangegrotto.wealthdraft.networth.projections.AssetChange;
import com.strangegrotto.wealthdraft.networth.projections.AssetSnapshotTransformer;

public class TestAssetType<S extends AssetSnapshot> {
    private final AssetSnapshotTransformer<S, AssetChange<S>> transformer;

    public TestAssetType(AssetSnapshotTransformer<S, AssetChange<S>> transformer) {
        this.transformer = transformer;
    }

    public AssetSnapshotTransformer<S, AssetChange<S>> getTransformer(Class<S> clazz) {
        return this.transformer;
    }
}
