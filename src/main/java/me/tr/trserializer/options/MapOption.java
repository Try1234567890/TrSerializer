package me.tr.trserializer.options;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Represent an option that contains a {@link Map}.
 * <p>This implements {@link Map} so can be provided as a {@link Map} too.
 */
public class MapOption<K, V> extends Option<Map<K, V>> implements Map<K, V> {

    public MapOption() {
    }

    public MapOption(Map<K, V> value) {
        super(value);
    }

    @Override
    public int size() {
        return get().size();
    }

    @Override
    public boolean isEmpty() {
        return get().isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return get().containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return get().containsValue(value);
    }

    @Override
    public V get(Object key) {
        return get().get(key);
    }

    public V get(Predicate<K> key) {
        for (Map.Entry<K, V> entry : get().entrySet()) {
            if (key.test(entry.getKey()))
                return entry.getValue();
        }
        return null;
    }

    public V retrieve(K key) {
        return get().get(key);
    }

    public Optional<V> retrieve(Predicate<K> key) {
        return Optional.ofNullable(get(key));
    }

    @Override
    public V put(K key, V value) {
        return get().put(key, value);
    }

    @Override
    public V remove(Object key) {
        return get().remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        get().putAll(m);
    }

    @Override
    public void clear() {
        get().clear();
    }

    @Override
    public Set<K> keySet() {
        return get().keySet();
    }

    @Override
    public Collection<V> values() {
        return get().values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return get().entrySet();
    }
}
