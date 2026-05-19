package me.tr.trserializer.registries;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.*;
import java.util.stream.Stream;

public class Registry<K, V> {
    // ConcurrentHashMap is used by default to ensure thread-safety across the framework
    protected final Map<K, V> internalMap = new ConcurrentHashMap<>();

    /**
     * Registers a value associated with the specified key.
     * If the key already existed, the old value is overwritten.
     *
     * @return The previous value associated with the key, or null if there was none.
     */
    public V register(K key, V value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("Key and Value cannot be null");
        }
        return internalMap.put(key, value);
    }

    /**
     * Registers all entries from the provided map into this registry.
     */
    public void registerAll(Map<? extends K, ? extends V> map) {
        if (map == null) {
            throw new IllegalArgumentException("Map cannot be null");
        }
        map.forEach(this::register);
    }

    /**
     * Registers a value only if the key is not already associated with any value.
     *
     * @return The current (existing or computed) value associated with the specified key.
     */
    public V registerIfAbsent(K key, V value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("Key and Value cannot be null");
        }
        return internalMap.putIfAbsent(key, value);
    }

    /**
     * Registers a value computed dynamically via a Function if the key is absent.
     *
     * @return The current (existing or computed) value associated with the specified key.
     */
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        return internalMap.computeIfAbsent(key, mappingFunction);
    }

    /**
     * Unregisters and removes an item from the registry by its key.
     *
     * @return The removed value, or null if the key was not found.
     */
    public V unregister(K key) {
        return internalMap.remove(key);
    }

    /**
     * Unregisters an entry only if it is currently mapped to the specified value.
     *
     * @return true if the value was removed
     */
    public boolean unregisterIf(K key, V value) {
        return internalMap.remove(key, value);
    }

    /**
     * Removes all entries that match the given predicate based on their keys and values.
     */
    public void unregisterIf(BiPredicate<? super K, ? super V> filter) {
        internalMap.entrySet().removeIf(entry -> filter.test(entry.getKey(), entry.getValue()));
    }

    /**
     * Retrieves the value associated with the key.
     *
     * @return The value, or null if not found.
     */
    public V get(K key) {
        return internalMap.get(key);
    }

    /**
     * Returns an Optional containing the value associated with the key.
     * Prevents NullPointerExceptions and helps with fallback flows.
     */
    public Optional<V> getOptional(K key) {
        return Optional.ofNullable(internalMap.get(key));
    }

    /**
     * Returns the value associated with the key, or the specified default value
     * if the key is not present in the registry.
     */
    public V getOrDefault(K key, V defaultValue) {
        return internalMap.getOrDefault(key, defaultValue);
    }

    /**
     * Returns the value associated with the key, or throws a custom exception
     * provided by a Supplier if the key does not exist.
     */
    public <X extends Throwable> V getOrThrow(K key, Supplier<? extends X> exceptionSupplier) throws X {
        V value = internalMap.get(key);
        if (value == null) {
            throw exceptionSupplier.get();
        }
        return value;
    }

    /**
     * Finds the first registered value that matches a specific predicate.
     * Extremely useful to find an Addon or Handler that can handle a specific class/object.
     */
    public Optional<V> findFirst(Predicate<? super V> predicate) {
        return streamValues().filter(predicate).findFirst();
    }

    /**
     * Finds the first key whose value matches a specific predicate.
     */
    public Optional<K> findKey(Predicate<? super V> predicate) {
        return internalMap.entrySet().stream()
                .filter(entry -> predicate.test(entry.getValue()))
                .map(Map.Entry::getKey)
                .findFirst();
    }

    /**
     * Checks if a specific key is present in the registry.
     */
    public boolean containsKey(K key) {
        return internalMap.containsKey(key);
    }

    /**
     * Checks if a specific value is present in the registry.
     */
    public boolean containsValue(V value) {
        return internalMap.containsValue(value);
    }

    /**
     * Completely clears the registry.
     */
    public void clear() {
        internalMap.clear();
    }

    /**
     * Returns the number of currently registered elements.
     */
    public int size() {
        return internalMap.size();
    }

    /**
     * Checks if the registry is empty.
     */
    public boolean isEmpty() {
        return internalMap.isEmpty();
    }

    /**
     * Performs the given action for each entry in this registry until all entries
     * have been processed or the action throws an exception.
     */
    public void forEach(BiConsumer<? super K, ? super V> action) {
        internalMap.forEach(action);
    }

    /**
     * Replaces each entry's value with the result of invoking the given
     * function on that entry until all entries have been processed.
     */
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        internalMap.replaceAll(function);
    }

    // --- Exportation & Utility Streams ---

    /**
     * Returns an unmodifiable Set view of the keys contained in this registry.
     */
    public Set<K> keySet() {
        return internalMap.keySet();
    }

    /**
     * Returns an unmodifiable Collection view of the values contained in this registry.
     */
    public Collection<V> values() {
        return internalMap.values();
    }

    /**
     * Returns a thread-safe shallow copy of the internal map.
     */
    public Map<K, V> asMap() {
        return new ConcurrentHashMap<>(internalMap);
    }

    /**
     * Provides a Stream of the registered values, ideal for advanced pipeline filtering.
     */
    public Stream<V> streamValues() {
        return internalMap.values().stream();
    }

    /**
     * Provides a Stream of the registered keys.
     */
    public Stream<K> streamKeys() {
        return internalMap.keySet().stream();
    }
}