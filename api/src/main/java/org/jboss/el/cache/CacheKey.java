/*
 * Copyright The JBoss Jakarta EL API Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.jboss.el.cache;

/** Classloader plus classname tuple used as a key in caches used in this package. */
final class CacheKey {
    final ClassLoader loader;
    private final String className;

    CacheKey(final ClassLoader loader, final String className) {
        this.loader = loader;
        this.className = className;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final CacheKey cacheKey = (CacheKey) o;

        if (className != null ? !className.equals(cacheKey.className) : cacheKey.className != null) return false;
        if (loader != null ? !loader.equals(cacheKey.loader) : cacheKey.loader != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = loader != null ? loader.hashCode() : 0;
        result = 31 * result + (className != null ? className.hashCode() : 0);
        return result;
    }
}
