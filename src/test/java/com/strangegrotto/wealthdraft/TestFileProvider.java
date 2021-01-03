package com.strangegrotto.wealthdraft;

import java.net.URL;

/**
 * Gets the URL of a file on the classpath
 */
public interface TestFileProvider {
    String getContainingDirname();

    String getFilename();

    default URL getResource() {
        return getClass().getClassLoader().getResource(getContainingDirname() + "/" + getFilename());
    }
}
