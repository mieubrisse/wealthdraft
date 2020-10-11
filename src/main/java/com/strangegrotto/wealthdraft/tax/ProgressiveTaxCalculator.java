package com.strangegrotto.wealthdraft.tax;

import com.google.common.collect.Ordering;
import com.strangegrotto.wealthdraft.govconstants.TaxBracket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ProgressiveTaxCalculator {
    private static final Logger LOG = LoggerFactory.getLogger(ProgressiveTaxCalculator.class);

    private final List<TaxBracket> brackets;

    public ProgressiveTaxCalculator(List<TaxBracket> brackets) {
        List<TaxBracket> bracketsCopy = new ArrayList(brackets);

        // Store the brackets in REVERSE order, since we need it for calculating
        Comparator<TaxBracket> bracketComparator = Comparator.comparing(
                bracket -> bracket.getFloor()
        );
        Ordering<TaxBracket> taxBracketOrdering = Ordering.from(bracketComparator)
                .reverse();
        bracketsCopy.sort(taxBracketOrdering);
        this.brackets = bracketsCopy;
    }

    // TODO switch from using double (which has precision errors) to using BigDecimal or something similar
    public double calculateTax(long amount) {
        long remainingToCalculateTaxFor = amount;
        double totalTax = 0;
        for (TaxBracket bracket : this.brackets) {
            long floor = bracket.getFloor();
            double rate = bracket.getRate();
            LOG.debug("Floor: {}, Rate: {}", floor, rate);

            long amountInBracket = Math.max(0, remainingToCalculateTaxFor - floor);
            double taxInBracket = (double)amountInBracket * rate;
            totalTax += taxInBracket;
            remainingToCalculateTaxFor -= amountInBracket;

            LOG.debug("Tax so far: {}", totalTax);
        }
        return totalTax;
    }
}
