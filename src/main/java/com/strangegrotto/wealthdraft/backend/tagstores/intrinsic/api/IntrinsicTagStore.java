package com.strangegrotto.wealthdraft.backend.tagstores.intrinsic.api;

import java.util.Map;
import java.util.Set;

public interface IntrinsicTagStore {
    Map<String, Set<String>> getTags();
}
