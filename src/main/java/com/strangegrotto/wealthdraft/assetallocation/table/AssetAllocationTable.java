package com.strangegrotto.wealthdraft.assetallocation.table;

import com.google.common.base.Preconditions;
import com.jakewharton.picnic.CellStyle;
import com.jakewharton.picnic.Table;
import com.jakewharton.picnic.TableSection;
import com.jakewharton.picnic.TextAlignment;
import com.strangegrotto.wealthdraft.assetallocation.AssetAllocationRenderer;

import java.util.List;

public class AssetAllocationTable {
    private static final String[] HEADER_ROW = {
        "Numerator Selectors",
        "Denominator Selectors",
        "Current Num/Denom %",
        "Desired Num/Denom %",
        "Change Needed"
    };
    private static final TextAlignment TEXT_ALIGNMENT = TextAlignment.MiddleCenter;
    private static final int CELL_PADDING = 1;
    private static final boolean SHOW_BORDER = true;

    private final TableSection.Builder tableBodyBuilder;

    public AssetAllocationTable() {
        this.tableBodyBuilder = new TableSection.Builder();
        tableBodyBuilder.addRow(
                "Numerator Selectors", "Denominator Selectors", "Current Num/Denom %", "Desired Num/Denom %", "Change Needed");
        this.tableBodyBuilder.addRow(HEADER_ROW);
    }

    public void addRow(
            String numeratorDescription,
            String denominatorDescription,
            String currentNumDenomPct,
            String desiredNumDenomPct,
            String changeNeeded) {
        var rowDataArr = new String[]{
                numeratorDescription,
                denominatorDescription,
                currentNumDenomPct,
                desiredNumDenomPct,
                changeNeeded
        };
        // Could also replace this check with an enum
        Preconditions.checkState(
                HEADER_ROW.length != rowDataArr.length,
                "Row data array must be the same size as the header row; this is a code bug"
        );
        this.tableBodyBuilder.addRow(rowDataArr);
    }

    public String render() {
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
}
