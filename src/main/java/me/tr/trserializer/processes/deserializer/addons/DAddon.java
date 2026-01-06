package me.tr.trserializer.processes.deserializer.addons;

import me.tr.trserializer.processes.deserializer.Deserializer;
import me.tr.trserializer.processes.process.Process;
import me.tr.trserializer.processes.process.insert.InsertMethod;
import me.tr.trserializer.processes.process.addons.Priority;
import me.tr.trserializer.processes.process.addons.PAddon;
import me.tr.trserializer.types.GenericType;

import java.lang.reflect.Field;
import java.util.Optional;

public abstract class DAddon extends PAddon {

    public DAddon(String name, Priority priority, InsertMethod insert) {
        super(name, priority, insert);
    }

    public DAddon(String name, InsertMethod insert) {
        super(name, insert);
    }

    public DAddon(String name, Priority priority) {
        super(name, priority);
    }

    public DAddon(String name) {
        super(name);
    }

    public abstract Optional<Object> process(Deserializer deserializer, Object obj, GenericType<?> type, Field field) throws Exception;

    @Override
    public Optional<Object> process(Process process, Object obj, GenericType<?> type, Field field) throws Exception {
        return process((Deserializer) process, obj, type, field);
    }
}
