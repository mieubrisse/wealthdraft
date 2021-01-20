package com.strangegrotto.wealthdraft.backend.assethistory.impl;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.common.base.Preconditions;
import com.strangegrotto.wealthdraft.AbstractYmlBackedStoreFactory;
import com.strangegrotto.wealthdraft.backend.assets.api.AssetsStore;

import java.time.LocalDate;

public class SimpleAssetHistoryStoreFactory extends AbstractYmlBackedStoreFactory<
        SerAssetsHistory,
        SerAssetsHistory,
        SimpleAssetHistoryStore> {
    private final AssetsStore assetsStore;

    public SimpleAssetHistoryStoreFactory(AssetsStore assetsStore) {
        this.assetsStore = assetsStore;
    }

    @Override
    protected void configureMapper(ObjectMapper mapper) {
        var module = new SimpleModule();
        module.addDeserializer(SerAssetsHistory.class, new SerAssetsHistoryDeserializer(this.assetsStore));
        mapper.registerModule(module);
        return;
    }

    @Override
    protected JavaType getDeserializationType(TypeFactory typeFactory) {
        return typeFactory.constructType(SerAssetsHistory.class);
    }

    @Override
    protected SerAssetsHistory postprocess(SerAssetsHistory deserialized) {
        return deserialized;
    }

    @Override
    protected void validate(SerAssetsHistory postprocessed) {
        var assets = this.assetsStore.getAssets();

        LocalDate today = LocalDate.now();
        var history = postprocessed.getHistory();

        // Verify that all assets in the history are in the asset list
        for (var historyEntry : history.entrySet()) {
            var date = historyEntry.getKey();
            var assetSnapshots = historyEntry.getValue();
            Preconditions.checkState(
                    !date.isAfter(today),
                    "Found a historical record with date '%s', which is in the future",
                    date
            );

            for (String assetId : assetSnapshots.keySet()) {
                Preconditions.checkState(
                        assets.containsKey(assetId),
                        "Asset '%s' appears in the history on '%s' but isn't defined in the assets list",
                        assetId,
                        date
                );
            }
        }
    }

    @Override
    protected SimpleAssetHistoryStore buildResult(SerAssetsHistory postprocessed) {
        return new SimpleAssetHistoryStore(postprocessed.getHistory());
    }
}
