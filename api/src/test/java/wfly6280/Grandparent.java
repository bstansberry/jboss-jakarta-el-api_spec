/*
 * Copyright The JBoss Jakarta EL API Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package wfly6280;

import java.util.Collection;

/** Package protected to have public methods bridged in public subclasses */
class Grandparent<K, V> implements Holder<K, V> {

    @Override public void hold(K key, V value) {
    }

    @Override public Collection<V> values(K key) {
        return null;
    }
}