package com.strangegrotto.wealthdraft.tagstores.custom.api.types;

import com.google.common.base.Strings;

public class TagNotFoundException extends Exception {
    public TagNotFoundException(String tagName) {
        super(Strings.lenientFormat("Tag '%s' doesn't exist", tagName));
    }
}
