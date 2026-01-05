package me.tr.trserializer.processes.deserializer;

import java.util.HashMap;

public class DeserializerCache {
    private final Deserializer process;
    private final HashMap<Object, Object> cache = new HashMap<>();

    public DeserializerCache(Deserializer process) {
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

    public Deserializer getProcess() {
        return process;
    }

    @Override
    public String toString() {
        return cache.toString();
    }
}
