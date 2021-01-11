package com.strangegrotto.wealthdraft.assethistory.impl;

import com.strangegrotto.wealthdraft.assethistory.api.types.AssetSnapshot;
import com.strangegrotto.wealthdraft.errors.ValOrGerr;
import com.strangegrotto.wealthdraft.projections.impl.temporal.AssetChange;

import java.math.BigDecimal;

/**
 * Use this class to implement {@link AssetSnapshot}
 * @param <CHANGE> The type of asset change that the snapshot will consume/produce
 */
public abstract class AbstractAssetSnapshot<CHANGE extends AssetChange> implements AssetSnapshot<CHANGE> {
    private final Class<CHANGE> changeClass;

    protected AbstractAssetSnapshot(Class<CHANGE> changeClass) {
        this.changeClass = changeClass;
    }

    public abstract BigDecimal getValue();

    public abstract AssetSnapshot<CHANGE> projectOneMonth();

    // AssetChange must be unparameterized because we won't know the AssetChange's type until runtime
    //  due to it being deserialized from YAML. This is final to ensure that the casting is done.
    @Override
    public final ValOrGerr<AssetSnapshot<CHANGE>> applyChange(AssetChange change) throws ClassCastException {
        if (!this.changeClass.isInstance(change)) {
            throw new ClassCastException("Change class '" + change.getClass() + "' not of accepted class type '" + this.changeClass + "'");
        }
        CHANGE castedChange = this.changeClass.cast(change);
        return applyChangeInternal(castedChange);
    }

    protected abstract ValOrGerr<AssetSnapshot<CHANGE>> applyChangeInternal(CHANGE change);
}
