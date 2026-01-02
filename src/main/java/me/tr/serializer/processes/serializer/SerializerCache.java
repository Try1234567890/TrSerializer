package me.tr.serializer.processes.serializer;

import me.tr.serializer.processes.ProcessCache;
import me.tr.serializer.utility.Utility;

import java.util.IdentityHashMap;

public class SerializerCache extends ProcessCache<Object, Integer> {

    public SerializerCache(Serializer serializer) {
        super(serializer, new IdentityHashMap<>());
    }

    public boolean isCachable(Object o) {
        if (o == null) return false;
        if (o instanceof String) return false;
        if (o.getClass().isPrimitive()) return false;
        if (Utility.isWrapper(o)) return false;

        return true;
    }

    public void cache(Object key) {
        super.cache(key, nextID());
    }

    @Override
    public Serializer getProcess() {
        return (Serializer) super.getProcess();
    }

    public Serializer getSerializer() {
        return getProcess();
    }
}
