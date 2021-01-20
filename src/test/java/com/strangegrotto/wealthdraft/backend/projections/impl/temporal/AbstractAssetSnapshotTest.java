package com.strangegrotto.wealthdraft.backend.projections.impl.temporal;

import com.strangegrotto.wealthdraft.assetimpls.bankaccount.BankAccountAssetChange;
import com.strangegrotto.wealthdraft.assetimpls.bankaccount.ImmBankAccountAssetChange;
import com.strangegrotto.wealthdraft.assetimpls.stock.ImmStockAssetSnapshot;
import com.strangegrotto.wealthdraft.assetimpls.stock.StockAssetSnapshot;
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
