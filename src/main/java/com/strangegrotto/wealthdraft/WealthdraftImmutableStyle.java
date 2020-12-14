package com.strangegrotto.wealthdraft;

import org.immutables.value.Value;

@Value.Style(
        allMandatoryParameters = true,
        stagedBuilder = true,
        optionalAcceptNullable = true,

        // We intentionally want this off because it can lead to a stack overflow error in the
        //  asset snapshot project methods, which return an instance of the same class
        defaultAsDefault = false,

        typeImmutable = "Imm*",
        typeImmutableEnclosing = "Imm*"
)
public @interface WealthdraftImmutableStyle { }
