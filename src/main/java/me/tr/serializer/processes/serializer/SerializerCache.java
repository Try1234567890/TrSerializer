package me.tr.serializer.processes.serializer;

import java.util.IdentityHashMap;

public class SerializerCache {
    private final Serializer process;
    private final IdentityHashMap<Object, Object> cache = new IdentityHashMap<>();

    public SerializerCache(Serializer process) {
        this.process = process;
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

    public Serializer getProcess() {
        return process;
    }
    @Override
    public String toString() {
        return cache.toString();
    }
}
