package me.tr.trserializer.processes.process.addons;

import me.tr.trserializer.processes.process.Process;
import me.tr.trserializer.types.GenericType;

import java.lang.reflect.Field;
import java.util.Optional;

public abstract class ProcessAddon {
    private final String name;
    private final Priority priority;

    public ProcessAddon(String name, Priority priority) {
        this.name = name;
        this.priority = priority;
    }

    public ProcessAddon(String name) {
        this.name = name;
        this.priority = Priority.NORMAL;
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
