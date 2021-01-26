package com.strangegrotto.wealthdraft.backend.assethistory.impl;

import com.strangegrotto.wealthdraft.assetimpls.bankaccount.BankAccountAssetChange;
import com.strangegrotto.wealthdraft.assetimpls.bankaccount.ImmBankAccountAssetChange;
import com.strangegrotto.wealthdraft.assetimpls.stock.ImmStockAssetSnapshot;
import com.strangegrotto.wealthdraft.assetimpls.stock.StockAssetSnapshot;
import com.strangegrotto.wealthdraft.backend.projections.impl.temporal.AssetParameterChangeValueOperation;
import com.strangegrotto.wealthdraft.backend.projections.impl.temporal.ImmSerAssetParameterChange;
import org.junit.Test;

import java.math.BigDecimal;

public class AbstractAssetSnapshotTest {
    @Test(expected = ClassCastException.class)
    public void testClassCastExceptionThrownOnIncompatibleTypes() {
        StockAssetSnapshot stockAssetSnapshot = ImmStockAssetSnapshot.of(
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(10)
        );
        BankAccountAssetChange bankAssetChange = ImmBankAccountAssetChange.of(
                ImmSerAssetParameterChange.of(BigDecimal.valueOf(10), AssetParameterChangeValueOperation.ADD),
                null
        );
        stockAssetSnapshot.applyChange(bankAssetChange);
    }
}
