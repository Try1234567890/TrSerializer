package me.tr.serializer.processes;

import java.util.Iterator;
import java.util.Map;

public class ProcessCache<K, V> implements Iterable<V> {
    private final Process process;
    private final Map<K, V> cache;
    private int id = 0;

    public ProcessCache(Process process, Map<K, V> cache) {
        this.process = process;
        this.cache = cache;
    }

    public V retrieve(K key) {
        return cache.get(key);
    }

    public void cache(K key, V value) {
        cache.put(key, value);
    }

    public V remove(K key) {
        return cache.remove(key);
    }

    public boolean has(K key) {
        return cache.containsKey(key);
    }

    public void clear() {
        cache.clear();
    }

    public int nextID() {
        return id++;
    }

    @Override
    public Iterator<V> iterator() {
        return cache.values().iterator();
    }

    public Process getProcess() {
        return process;
    }
}
