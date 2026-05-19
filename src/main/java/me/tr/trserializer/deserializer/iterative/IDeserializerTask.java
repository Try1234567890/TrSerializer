package me.tr.trserializer.deserializer.iterative;

import me.tr.trserializer.deserializer.DeserializerTask;
import me.tr.trserializer.deserializer.helper.typeResolver.DeserializerTaskTypeResolver;
import me.tr.trserializer.types.GenericType;

import java.util.Stack;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * This class represent a task for the iterative implementation
 * of the serializer. Indeed, extend the {@link DeserializerTask}
 * abstract class and implement the reference to the {@link Stack}
 * of {@link IDeserializerTask} that wait to be serialized.
 */
public class IDeserializerTask extends DeserializerTask {
    private final Stack<IDeserializerTask> tasks;
    private final DeserializerTaskTypeResolver typeResolver;

    public IDeserializerTask(UUID id, IDeserializer deserializer, Object object, GenericType<?> type, Consumer<Object> result, Stack<IDeserializerTask> tasks) {
        super(id, deserializer, object, type, result);
        this.tasks = tasks;

        this.typeResolver = new DeserializerTaskTypeResolver(this);
    }

    public IDeserializerTask(IDeserializer deserializer, Object object, GenericType<?> type, Consumer<Object> result, Stack<IDeserializerTask> tasks) {
        super(deserializer, object, type, result);
        this.tasks = tasks;

        this.typeResolver = new DeserializerTaskTypeResolver(this);
    }

    public Stack<IDeserializerTask> getTasks() {
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
    public void deserialize(Object object, GenericType<?> type, Consumer<Object> result) {
        IDeserializerTask task = new IDeserializerTask(getDeserializer(), object, type, result, getTasks());
        getTasks().add(task);
    }

    @Override
    public IDeserializer getTranslator() {
        return (IDeserializer) super.getTranslator();
    }

    @Override
    public IDeserializer getDeserializer() {
        return (IDeserializer) super.getDeserializer();
    }

    public DeserializerTaskTypeResolver getTypeResolver() {
        return typeResolver;
    }
}
