/*
 * Copyright The JBoss Jakarta EL API Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.jboss.el.cache;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks Class.forName failures on behalf of {@link jakarta.el.ImportHandler}
 * in a cache that holds elements for the life of their relevant deployment.
 */
public final class ImportHandlerCache {

    private static final Set<CacheKey> MISSES = ConcurrentHashMap.newKeySet();

    public static boolean hasClassloadingMiss(ClassLoader loader, String className) {
        return MISSES.contains(new CacheKey(loader, className));
    }

    public static void recordClassloadingMiss(ClassLoader loader, String className) {
        MISSES.add(new CacheKey(loader, className));
    }

    static void clearClassLoader(final ClassLoader classLoader) {
        MISSES.removeIf(key -> key.loader == classLoader);
    }

    private ImportHandlerCache() {
        // no instances
    }
}
