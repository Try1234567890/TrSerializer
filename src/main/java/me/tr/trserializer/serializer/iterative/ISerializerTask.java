package me.tr.trserializer.serializer.iterative;

import me.tr.trserializer.serializer.Serializer;
import me.tr.trserializer.serializer.SerializerTask;
import me.tr.trserializer.serializer.helpers.savable.SerializerTaskSavabilityChecker;
import me.tr.trserializer.types.GenericType;

import java.util.Stack;
import java.util.UUID;
import java.util.function.Consumer;

public class ISerializerTask extends SerializerTask {
    private final Stack<SerializerTask> tasks;

    public ISerializerTask(UUID id, ISerializer serializer, Object object, GenericType<?> type, Consumer<Object> result,
                           SerializerTaskSavabilityChecker savabilityChecker, Stack<SerializerTask> tasks) {
        super(id, serializer, object, type, result, savabilityChecker);
        this.tasks = tasks;
    }

    public ISerializerTask(ISerializer serializer, Object object, GenericType<?> type, Consumer<Object> result,
                           SerializerTaskSavabilityChecker savabilityChecker, Stack<SerializerTask> tasks) {
        super(serializer, object, type, result, savabilityChecker);
        this.tasks = tasks;
    }

    public ISerializerTask(UUID id, ISerializer serializer, Object object, GenericType<?> type, Consumer<Object> result,
                           Stack<SerializerTask> tasks) {
        super(id, serializer, object, type, result);
        this.tasks = tasks;
    }

    public ISerializerTask(ISerializer serializer, Object object, GenericType<?> type, Consumer<Object> result,
                           Stack<SerializerTask> tasks) {
        super(serializer, object, type, result);
        this.tasks = tasks;
    }

    public ISerializerTask(ISerializer serializer, Object object, Consumer<Object> result,
                           Stack<SerializerTask> tasks) {
        super(serializer, object, result);
        this.tasks = tasks;
    }

    @Override
    public void serialize(Object object, GenericType<?> type, Consumer<Object> result) {
        ISerializerTask subtask = new ISerializerTask(getSerializer(), object, type, result, getSavabilityChecker(), getTasks());
        getTasks().push(subtask);
    }

    public Stack<SerializerTask> getTasks() {
        return tasks;
    }

    public void scheduleTop() {
        getTasks().push(this);
    }

    public void schedule() {
        scheduleTop();
    }

    public void scheduleBottom() {
        getTasks().add(this);
    }

    @Override
    public ISerializer getSerializer() {
        return (ISerializer) super.getSerializer();
    }

    @Override
    public ISerializer getTranslator() {
        return (ISerializer) super.getTranslator();
    }
}