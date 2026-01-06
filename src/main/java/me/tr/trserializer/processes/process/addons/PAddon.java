package me.tr.trserializer.processes.process.addons;

import me.tr.trserializer.processes.process.Process;
import me.tr.trserializer.processes.process.insert.BaseInsert;
import me.tr.trserializer.processes.process.insert.InsertMethod;
import me.tr.trserializer.registries.InsertMethodsRegistry;
import me.tr.trserializer.types.GenericType;

import java.lang.reflect.Field;
import java.util.Optional;

public abstract class PAddon {
    private final String name;
    private final Priority priority;
    private final InsertMethod insert;

    public PAddon(String name, Priority priority, InsertMethod insert) {
        this.name = name;
        this.priority = priority;
        this.insert = insert;
    }

    public PAddon(String name, InsertMethod insert) {
        this(name, Priority.NORMAL, insert == null ? InsertMethodsRegistry.getMethod(BaseInsert.class) : insert);
    }

    public PAddon(String name, Priority priority) {
        this(name, priority == null ? Priority.NORMAL : priority, InsertMethodsRegistry.getMethod(BaseInsert.class));
    }

    public PAddon(String name) {
        this(name, InsertMethodsRegistry.getMethod(BaseInsert.class));
    }

    public String getName() {
        return name;
    }

    public Priority getPriority() {
        return priority;
    }

    public int getPriorityCode() {
        return getPriority().getCode();
    }

    public InsertMethod getInsert() {
        return insert;
    }

    /**
     * Process the provided object.
     *
     * @param process The process that is working on this object.
     * @param obj     The object to process.
     * @param type    The object type.
     * @param field   The field of the value, {@code can be null!}
     * @return The new value of the provided object, or else {@link Optional#empty()}
     * @throws Exception If any error occurs while processing the object.
     */
    public abstract Optional<Object> process(Process process, Object obj, GenericType<?> type, Field field) throws Exception;
}
