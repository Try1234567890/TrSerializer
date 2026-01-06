package me.tr.trserializer.processes.serializer;

import me.tr.trserializer.processes.process.ProcessCache;

import java.util.IdentityHashMap;

public class SerializerCache extends ProcessCache {

    public SerializerCache(Serializer process) {
        super(process, new IdentityHashMap<>());
    }

    @Override
    public Serializer getProcess() {
        return (Serializer) super.getProcess();
    }

    public Serializer getSerializer() {
        return getProcess();
    }
}
