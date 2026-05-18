package me.tr.trserializer.serializer.iterative;

import me.tr.trserializer.serializer.helpers.savable.SerializerTaskSavabilityChecker;
import me.tr.trserializer.translator.FieldTask;
import me.tr.trserializer.translator.TranslatorTask;
import me.tr.trserializer.types.GenericType;

import java.lang.reflect.Field;
import java.util.Stack;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * This class represent the effective implementation
 * of the field variant of {@link ISerializerTask}.
 * <p>
 * A field task is an extension of {@link TranslatorTask} that contains
 * a reference to the current {@link Field}.
 * In default implemented translator is created when an object
 * is serializing as a map or an object is deserializing from a map.
 */
public class ISerializerFieldTask extends ISerializerTask implements FieldTask {
    private final Field field;

    public ISerializerFieldTask(UUID id, ISerializer serializer, Object object, GenericType<?> type, Consumer<Object> result,
                                SerializerTaskSavabilityChecker savabilityChecker, Stack<ISerializerTask> tasks, Field field) {
        super(id, serializer, object, type, result, savabilityChecker, tasks);
        this.field = field;
    }

    public ISerializerFieldTask(ISerializer serializer, Object object, GenericType<?> type, Consumer<Object> result,
                                SerializerTaskSavabilityChecker savabilityChecker, Stack<ISerializerTask> tasks, Field field) {
        super(serializer, object, type, result, savabilityChecker, tasks);
        this.field = field;
    }

    public ISerializerFieldTask(UUID id, ISerializer serializer, Object object, GenericType<?> type, Consumer<Object> result,
                                Stack<ISerializerTask> tasks, Field field) {
        super(id, serializer, object, type, result, tasks);
        this.field = field;
    }

    public ISerializerFieldTask(ISerializer serializer, Object object, GenericType<?> type, Consumer<Object> result,
                                Stack<ISerializerTask> tasks, Field field) {
        super(serializer, object, type, result, tasks);
        this.field = field;
    }

    public ISerializerFieldTask(ISerializer serializer, Object object, Consumer<Object> result, Stack<ISerializerTask> tasks, Field field) {
        super(serializer, object, result, tasks);
        this.field = field;
    }

    @Override
    public void serialize(Object object, GenericType<?> type, Consumer<Object> result) {
        ISerializerFieldTask subtask = new ISerializerFieldTask(getSerializer(), object, type, result, getTasks(), field);
        getTasks().push(subtask);
    }

    public void serialize(Object object, Consumer<Object> result) {
        ISerializerFieldTask subtask = new ISerializerFieldTask(getSerializer(), object, result, getTasks(), field);
        getTasks().push(subtask);
    }

    /**
     * @return the field assigned to this task.
     */
    @Override
    public Field getField() {
        return field;
    }
}
