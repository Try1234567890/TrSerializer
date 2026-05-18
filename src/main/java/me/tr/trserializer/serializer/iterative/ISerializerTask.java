package me.tr.trserializer.serializer.iterative;

import me.tr.trserializer.serializer.SerializerTask;
import me.tr.trserializer.serializer.helpers.savable.SerializerTaskSavabilityChecker;
import me.tr.trserializer.types.GenericType;

import java.util.Stack;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * This class represent a task for the iterative implementation
 * of the serializer. Indeed, extend the {@link SerializerTask}
 * abstract class and implement the reference to the {@link Stack}
 * of {@link ISerializerTask} that wait to be serialized.
 */
public class ISerializerTask extends SerializerTask {
    private final Stack<ISerializerTask> tasks;

    public ISerializerTask(UUID id, ISerializer serializer, Object object, GenericType<?> type, Consumer<Object> result,
                           SerializerTaskSavabilityChecker savabilityChecker, Stack<ISerializerTask> tasks) {
        super(id, serializer, object, type, result, savabilityChecker);
        this.tasks = tasks;
    }

    public ISerializerTask(ISerializer serializer, Object object, GenericType<?> type, Consumer<Object> result,
                           SerializerTaskSavabilityChecker savabilityChecker, Stack<ISerializerTask> tasks) {
        super(serializer, object, type, result, savabilityChecker);
        this.tasks = tasks;
    }

    public ISerializerTask(UUID id, ISerializer serializer, Object object, GenericType<?> type, Consumer<Object> result,
                           Stack<ISerializerTask> tasks) {
        super(id, serializer, object, type, result);
        this.tasks = tasks;
    }

    public ISerializerTask(ISerializer serializer, Object object, GenericType<?> type, Consumer<Object> result,
                           Stack<ISerializerTask> tasks) {
        super(serializer, object, type, result);
        this.tasks = tasks;
    }

    public ISerializerTask(ISerializer serializer, Object object, Consumer<Object> result,
                           Stack<ISerializerTask> tasks) {
        super(serializer, object, result);
        this.tasks = tasks;
    }

    @Override
    public void serialize(Object object, GenericType<?> type, Consumer<Object> result) {
        ISerializerTask subtask = new ISerializerTask(getSerializer(), object, type, result, getSavabilityChecker(), getTasks());
        getTasks().push(subtask);
    }

    public void serialize(Object object, Consumer<Object> result) {
        ISerializerTask subtask = new ISerializerTask(getSerializer(), object, result, getTasks());
        getTasks().push(subtask);
    }

    /**
     * @return retrieve the tasks of the main process.
     */
    public Stack<ISerializerTask> getTasks() {
        return tasks;
    }

    /**
     * Schedule the task at the top of the stack (max priority).
     */
    public void scheduleTop() {
        getTasks().push(this);
    }

    /**
     * Schedule the task at the top of the stack (max priority).
     */
    public void schedule() {
        scheduleTop();
    }

    /**
     * Schedule the task at the top of the stack (min priority).
     */
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