package com.strangegrotto.wealthdraft;

import java.net.URL;

/**
 * Gets the URL of a file on the classpath
 */
public interface TestFileProvider {
    URL getResource();
}
