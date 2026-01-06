package me.tr.trserializer.processes.serializer.addons;

import me.tr.trserializer.processes.process.Process;
import me.tr.trserializer.processes.process.addons.PAddon;
import me.tr.trserializer.processes.serializer.Serializer;
import me.tr.trserializer.types.GenericType;

import java.lang.reflect.Field;
import java.util.Optional;

public abstract class SAddon extends PAddon {

    public SAddon(String name) {
        super(name);
    }

    public abstract Optional<Object> process(Serializer serializer, Object obj, GenericType<?> type) throws Exception;

    @Override
    public Optional<Object> process(Process process, Object obj, GenericType<?> type, Field field) throws Exception {
        return process((Serializer) process, obj, type);
    }
}
