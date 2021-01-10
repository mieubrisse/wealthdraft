package com.strangegrotto.wealthdraft;

import java.net.URL;

/**
 * Gets the URL of a file on the classpath
 */
public interface TestFileProvider {
    TestResourceDirnames getContainingDirname();

    String getFilename();

    default URL getResource() {
        return getClass().getClassLoader().getResource(getContainingDirname().getDirname() + "/" + getFilename());
    }
}
