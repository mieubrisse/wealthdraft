package com.strangegrotto.wealthdraft.validator;

import com.google.common.base.Preconditions;
import com.strangegrotto.wealthdraft.govconstants.TaxBracket;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Class of static validation methods
 */
public class DeserializationValidator {
    private DeserializationValidator(){}

    public static void checkIsRatio(String fieldName, Double value) {
        if (value < 0 || value > 1) {
            throw new IllegalArgumentException(
                    String.format(
                            "Field '%s' must be in range [0.0, 1.0] but was %s",
                            fieldName,
                            value
                    )
            );
        }
    }

    public static void checkUniqueBracketFloors(String fieldName, List<TaxBracket> brackets) {
        Set<Long> floorSet = brackets.stream()
                .map(bracket -> bracket.getFloor())
                .collect(Collectors.toSet());
        Preconditions.checkState(
                floorSet.size() == brackets.size(),
                "Tax brackets for '%s' have more than one bracket with the same floor",
                fieldName
        );
    }
}
