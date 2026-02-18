package me.tr.trserializer.processes.process.cache;

import java.util.IdentityHashMap;

import me.tr.trserializer.processes.process.Process;

import java.util.Map;

public class Cache<K, V> {
    private final Process process;
    private boolean disabled;
    private Map<K, V> cache;

    public Cache(Process process, Map<K, V> cache) {
        this.process = process;
        this.cache = cache;
    }

    public V get(K key) {
        return getCache().get(key);
    }

    public void put(K key, V value) {
        getCache().put(key, value);
    }

    public void remove(K key) {
        getCache().remove(key);
    }

    public boolean has(K key) {
        return getCache().containsKey(key);
    }

    public void clear() {
        getCache().clear();
    }

    protected Map<K, V> getCache() {
        if (cache == null)
            this.cache = new IdentityHashMap<>();
        return cache;
    }

    public boolean isEnabled() {
        return !isDisabled();
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void disable() {
        setStatus(true);
    }

    public void enable() {
        setStatus(false);
    }

    public void setStatus(boolean status) {
        this.disabled = status;
    }

    public boolean isCachable(K original, V result) {
        return isCacheable(original) && isCacheable(result);
    }

    private boolean isCacheable(Object obj) {
        return obj != null &&
                !obj.getClass().isPrimitive() &&
                !(obj instanceof String) &&
                !obj.getClass().isEnum();
    }

    public Process getProcess() {
        return process;
    }

    @Override
    public String toString() {
        return getCache().toString();
    }
}
