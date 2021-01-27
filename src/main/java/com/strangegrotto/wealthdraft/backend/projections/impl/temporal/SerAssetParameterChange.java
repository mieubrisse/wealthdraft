package com.strangegrotto.wealthdraft.backend.projections.impl.temporal;

import com.strangegrotto.wealthdraft.WealthdraftImmutableStyle;
import com.strangegrotto.wealthdraft.backend.projections.api.types.AssetParameterChange;
import com.strangegrotto.wealthdraft.errors.ValOrGerr;
import org.immutables.value.Value;

import java.math.BigDecimal;

@WealthdraftImmutableStyle
@Value.Immutable
public interface SerAssetParameterChange extends AssetParameterChange {
    BigDecimal getValue();

    AssetParameterChangeValueOperation getOperation();

    @Override
    @Value.Derived
    default ValOrGerr<BigDecimal> apply(BigDecimal oldValue) {
        ValOrGerr<BigDecimal> result = getOperation().apply(oldValue, getValue());
        if (result.hasGerr()) {
            return ValOrGerr.propGerr(
                    result.getGerr(),
                    "An error occurred applying operation '{}' to asset",
                    getOperation()
            );
        }
        return result;
    }

    // TODO Build toString that's the original parse
}
