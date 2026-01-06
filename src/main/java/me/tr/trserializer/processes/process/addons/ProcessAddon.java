package me.tr.trserializer.processes.process.addons;

import me.tr.trserializer.processes.process.Process;
import me.tr.trserializer.types.GenericType;

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

    public abstract Optional<Object> process(Process process, Object obj, GenericType<?> type) throws Exception;
}
