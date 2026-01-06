package me.tr.trserializer.processes.deserializer;

import me.tr.trserializer.processes.process.ProcessCache;

import java.util.HashMap;

public class DeserializerCache extends ProcessCache {

    public DeserializerCache(Deserializer deserializer) {
        super(deserializer, new HashMap<>());
    }

    @Override
    public Deserializer getProcess() {
        return (Deserializer) super.getProcess();
    }
}
