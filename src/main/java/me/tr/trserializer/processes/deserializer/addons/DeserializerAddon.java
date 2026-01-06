package me.tr.trserializer.processes.deserializer.addons;

import me.tr.trserializer.processes.deserializer.Deserializer;
import me.tr.trserializer.processes.process.Process;
import me.tr.trserializer.processes.process.addons.Priority;
import me.tr.trserializer.processes.process.addons.ProcessAddon;
import me.tr.trserializer.processes.serializer.Serializer;
import me.tr.trserializer.types.GenericType;

import java.util.Optional;

public abstract class DeserializerAddon extends ProcessAddon {

    public DeserializerAddon(String name, Priority priority) {
        super(name, priority);
    }

    public DeserializerAddon(String name) {
        super(name);
    }

    public abstract Optional<Object> process(Deserializer deserializer, Object obj, GenericType<?> type) throws Exception;

    @Override
    public Optional<Object> process(Process process, Object obj, GenericType<?> type) throws Exception {
        return process((Deserializer) process, obj, type);
    }
}
