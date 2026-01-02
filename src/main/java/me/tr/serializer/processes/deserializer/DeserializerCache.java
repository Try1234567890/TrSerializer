package me.tr.serializer.processes.deserializer;

import me.tr.serializer.processes.ProcessCache;

import java.util.HashMap;

public class DeserializerCache extends ProcessCache<Integer, Object> {


    public DeserializerCache(Deserializer deserializer) {
        super(deserializer, new HashMap<>());
    }

    @Override
    public Deserializer getProcess() {
        return (Deserializer) super.getProcess();
    }

    public Deserializer getDeserializer() {
        return getProcess();
    }
}
