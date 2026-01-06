package me.tr.trserializer.processes.serializer.addons;

import me.tr.trserializer.processes.process.Process;
import me.tr.trserializer.processes.process.addons.ProcessAddon;
import me.tr.trserializer.processes.serializer.Serializer;
import me.tr.trserializer.types.GenericType;

import java.util.Optional;

public abstract class SerializerAddon extends ProcessAddon {

    public SerializerAddon(String name) {
        super(name);
    }

    public abstract Optional<Object> process(Serializer serializer, Object obj, GenericType<?> type) throws Exception;

    @Override
    public Optional<Object> process(Process process, Object obj, GenericType<?> type) throws Exception {
        return process((Serializer) process, obj, type);
    }
}
