package me.tr.trserializer.registries;

import java.util.Map;
import java.util.stream.Stream;


public abstract class Registry<K, V> {

    /**
     * Get the {@code Map<K, V>} that contains all the values of the registry.
     *
     * @return the {@code Map<K, V>} that contains all the values of the registry.
     */
    public abstract Map<K, V> getRegistry();

    /**
     * Get the value from the {@code Map<K, V>} with the provided identifier.
     *
     * @param name The value identifier name to get; This is case-sensitive.
     * @return If the identifier is found, return its value, otherwise null.
     */
    public V get(K name) {
        if (has(name)) {
            return getRegistry().get(name);
        } else {
            for (Map.Entry<K, V> entry : getRegistry().entrySet()) {
                if (equals(name, entry.getKey())) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }

    /**
     * Register the provided value to the registry.
     * <p>
     * If the registry already contains the value identifier,
     * a new {@link NotUniqueIdentifierException} will be thrown.
     *
     * @param value The value to register to the registry.
     * @throws NotUniqueIdentifierException if value registry already contains its identifier.
     */
    public void register(K id, V value) {
        if (getRegistry().containsKey(id)) {
            throw new NotUniqueIdentifierException("Identifier " + id + " is already used in registry " + value.getClass().getSimpleName());
        }
        getRegistry().put(id, value);
    }

    /**
     * Unregister the value with the provided identifier from the registry.
     * <p>
     * If the provided identifier is null, a new {@link NullPointerException} will be thrown.
     *
     * @param identifier The value identifier to remove.
     */
    public void unregister(K identifier) {
        getRegistry().remove(identifier);
    }

    /**
     * Modify the first value provided with the new one.
     * <p>
     * If one of the provided values is null, a new {@link NullPointerException} will be thrown.
     *
     * @param from The value to remove from the registry.
     * @param to   The value to add to the registry.
     * @throws NullPointerException if one of the values is null.
     * @see #unregister(K)
     * @see #register(K, V)
     */
    public void modify(K from, K newID, V to) {
        unregister(from);
        register(newID, to);
    }

    public boolean has(K identifier) {
        return getRegistry().containsKey(identifier);
    }

    public Stream<V> values() {
        return getRegistry().values().stream();
    }

    public Stream<K> keys() {
        return getRegistry().keySet().stream();
    }

    public boolean equals(K k1, K k2) {
        return k1.equals(k2);
    }

    @Override
    public String toString() {
        return getRegistry().toString();
    }
}