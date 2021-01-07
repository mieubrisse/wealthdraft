package com.strangegrotto.wealthdraft.assetallocation.renderer;

import com.google.common.base.Preconditions;
import com.jakewharton.picnic.CellStyle;
import com.jakewharton.picnic.Table;
import com.jakewharton.picnic.TableSection;
import com.jakewharton.picnic.TextAlignment;

class AssetAllocationTable {
    private static final AssetAllocationTableRow HEADER_ROW = ImmAssetAllocationTableRow.of(
        "Numerator Selectors",
        "Denominator Selectors",
        "Current Num/Denom %",
        "Desired Num/Denom %",
        "Change Needed",
        "Deviation %",
        "Deviation Status"
    );
    private static final TextAlignment TEXT_ALIGNMENT = TextAlignment.MiddleCenter;
    private static final int CELL_PADDING = 1;
    private static final boolean SHOW_BORDER = true;

    private final TableSection.Builder tableBodyBuilder;

    AssetAllocationTable() {
        this.tableBodyBuilder = new TableSection.Builder();
        this.tableBodyBuilder.addRow(getStrArrFromRow(HEADER_ROW));
    }

    void addRow(AssetAllocationTableRow row) {
        this.tableBodyBuilder.addRow(getStrArrFromRow(row));
    }

    String render() {
        var tableBody = this.tableBodyBuilder.build();
        var cellStyle = new CellStyle.Builder()
                .setAlignment(TEXT_ALIGNMENT)
                .setPadding(CELL_PADDING)
                .setBorder(SHOW_BORDER)
                .build();
        var table = new Table.Builder()
                .setCellStyle(cellStyle)
                .setBody(tableBody)
                .build();
        return table.toString();
    }

    private static String[] getStrArrFromRow(AssetAllocationTableRow row) {
        return new String[]{
                row.getNumeratorStr(),
                row.getDenominatorStr(),
                row.getCurrentNumDenomPct(),
                row.getTargetNumDenomPct(),
                row.getCorrectionNeeded(),
                row.getDeviationPct(),
                row.getDeviationStatus()
        };
    }
}
