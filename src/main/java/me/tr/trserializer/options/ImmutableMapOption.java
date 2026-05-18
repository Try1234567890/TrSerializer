package me.tr.trserializer.options;

import java.util.Map;

/**
 * Represent an option that contains a {@link Map}.
 * <p>This implements {@link Map} so can be provided as a {@link Map} too.
 * <p>
 * This implementation is immutable, so any remove, add or retain operation
 * will be ignored.
 */
public class ImmutableMapOption<K, V> extends MapOption<K, V> {
    @Override
    public V put(K key, V value) {
        return value;
    }

    @Override
    public V remove(Object key) {
        return null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
    }

    @Override
    public V putIfAbsent(K key, V value) {
        return value;
    }

    @Override
    public boolean remove(Object key, Object value) {
        return false;
    }

    @Override
    public void clear() {

    }
}
