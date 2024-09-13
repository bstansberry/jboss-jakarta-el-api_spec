/*
 * Copyright The JBoss Jakarta EL API Authors
 * SPDX-License-Identifier: Apache-2.0
 */
package wfly6280;

import java.util.List;

public interface ListHolder<K, V> extends Holder<K, V> {

    @Override void hold(K key, V value);

    @Override List<V> values(K key);
}