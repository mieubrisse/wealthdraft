package com.strangegrotto.wealthdraft;

import com.google.common.base.Strings;
import org.slf4j.Logger;

import java.text.DecimalFormat;

// TODO Eventually, make this the only way to write to the display (prob once we start doing more structured layout)
public class Display {
    private final Logger log;
    private final int minimumItemTitleWidth;
    private final int minimumCurrencyWidth;
    private final DecimalFormat currencyFormat;
    private final String sumLine;
    private final String bannerHeaderLine;

    public Display(Logger log, int minimumItemTitleWidth, int minimumCurrencyWidth, DecimalFormat currencyFormat) {
        this.log = log;
        this.minimumItemTitleWidth = minimumItemTitleWidth;
        this.minimumCurrencyWidth = minimumCurrencyWidth;
        this.currencyFormat = currencyFormat;

        this.sumLine = Strings.repeat(" ", this.minimumItemTitleWidth + 2)
                + Strings.repeat("-", this.minimumCurrencyWidth);
        this.bannerHeaderLine = Strings.repeat("=", 2 * this.minimumItemTitleWidth + 2);
    }

    public void printEmptyLine() {
        log.info("");
    }

    public void printBannerHeader(String header) {
        log.info(this.bannerHeaderLine);
        int spacesToAdd = Math.max(0, (this.bannerHeaderLine.length() - header.length()) / 2);
        log.info(Strings.repeat(" ", spacesToAdd) + header);
        log.info(this.bannerHeaderLine);
    }

    public void printSectionHeader(String header) {
        header = header.toUpperCase();
        // We add 2 to account for the ": " that each item entry has
        int totalWidth = 2 * this.minimumItemTitleWidth + 2;
        int spacesToAdd = Math.max(0, (totalWidth - header.length()) / 2);
        log.info(
                "{}{}",
                Strings.repeat(" ", spacesToAdd),
                header
        );
    }

    public void printSumLine() {
        log.info(this.sumLine);
    }

    public void printStringItem(String title, String value) {
        log.info(
                "{}: {}",
                String.format("%1$" + this.minimumItemTitleWidth + "s", title),
                value
        );
    }

    public void printCurrencyItem(String title, Object value) {
        printStringItem(
                title,
                String.format(
                        "%1$" + this.minimumCurrencyWidth + "s",
                        currencyFormat.format(value)
                )
        );
    }
}
