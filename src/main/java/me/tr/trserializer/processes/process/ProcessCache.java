package me.tr.trserializer.processes.process;

import java.util.Map;

public class ProcessCache {
    private final Process process;
    private final Map<Object, Object> cache;

    public ProcessCache(Process process,
                        Map<Object, Object> cache) {
        this.process = process;
        this.cache = cache;
    }

    public Object get(Object key) {
        return cache.get(key);
    }

    public void put(Object key, Object value) {
        cache.put(key, value);
    }

    public void remove(Object key) {
        cache.remove(key);
    }

    public boolean has(Object key) {
        return cache.containsKey(key);
    }

    public void clear() {
        cache.clear();
    }

    public boolean isCachable(Object original, Object result) {
        return (original != null && result != null) &&
                original != result &&
                !original.getClass().isPrimitive() ;
    }

    public Process getProcess() {
        return process;
    }

    @Override
    public String toString() {
        return cache.toString();
    }
}
