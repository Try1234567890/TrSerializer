package me.tr.trserializer.processes.serializer.addons;

import me.tr.trserializer.processes.process.Process;
import me.tr.trserializer.processes.serializer.helper.insert.InsertMethod;
import me.tr.trserializer.processes.process.addons.PAddon;
import me.tr.trserializer.processes.process.addons.Priority;
import me.tr.trserializer.processes.serializer.Serializer;
import me.tr.trserializer.types.GenericType;

import java.lang.reflect.Field;
import java.util.Optional;

public abstract class SAddon extends PAddon {
    private final InsertMethod insert;

    public SAddon(String name, Priority priority, InsertMethod insert) {
        super(name, priority);
        this.insert = insert;
    }

    public SAddon(String name, Priority priority) {
        this(name, priority, InsertMethod.BASE);
    }

    public SAddon(String name) {
        this(name, Priority.NORMAL, InsertMethod.BASE);
    }

    public InsertMethod getInsert() {
        return insert;
    }

    public abstract Optional<Object> process(Serializer process, Object obj, GenericType<?> type, Field field) throws Exception;

    @Override
    public Optional<Object> process(Process process, Object obj, GenericType<?> type, Field field) throws Exception {
        return process((Serializer) process, obj, type, field);
    }

}
