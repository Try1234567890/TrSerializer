package me.tr.trserializer.deserializer.iterative;

import me.tr.trserializer.deserializer.helper.typeResolver.StaticDeserializerTypeResolver;
import me.tr.trserializer.translator.FieldTask;
import me.tr.trserializer.translator.TranslatorTask;
import me.tr.trserializer.types.GenericType;

import java.lang.reflect.Field;
import java.util.Stack;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * This class represent the effective implementation
 * of the field variant of {@link IDeserializerTask}.
 * <p>
 * A field task is an extension of {@link TranslatorTask} that contains
 * a reference to the current {@link Field}.
 * In default implemented translator is created when an object
 * is serializing as a map or an object is deserializing from a map.
 */

public class IDeserializerFieldTask extends IDeserializerTask implements FieldTask {
    private final Field field;

    public IDeserializerFieldTask(UUID id, IDeserializer deserializer, Object object, GenericType<?> type, Consumer<Object> result,
                                  Stack<IDeserializerTask> tasks, Field field) {
        super(id, deserializer, object, type, result, tasks);
        this.field = field;
    }

    public IDeserializerFieldTask(IDeserializer deserializer, Object object, GenericType<?> type, Consumer<Object> result,
                                  Stack<IDeserializerTask> tasks, Field field) {
        super(deserializer, object, type, result, tasks);
        this.field = field;
    }

    public IDeserializerFieldTask(UUID id, IDeserializer deserializer, Object object, Consumer<Object> result, Stack<IDeserializerTask> tasks,
                                  Field field) {
        super(id, deserializer, object, StaticDeserializerTypeResolver.resolve(field), result, tasks);
        this.field = field;
    }

    public IDeserializerFieldTask(IDeserializer deserializer, Object object, Consumer<Object> result, Stack<IDeserializerTask> tasks,
                                  Field field) {
        super(deserializer, object, StaticDeserializerTypeResolver.resolve(field), result, tasks);
        this.field = field;
    }

    @Override
    public void deserialize(Object object, GenericType<?> type, Consumer<Object> result) {
        IDeserializerFieldTask task = new IDeserializerFieldTask(getDeserializer(), object, type, result, getTasks(), field);
        getTasks().add(task);
    }

    public void deserialize(Object object, Consumer<Object> result) {
        IDeserializerFieldTask task = new IDeserializerFieldTask(getDeserializer(), object, result, getTasks(), field);
        getTasks().add(task);
    }

    @Override
    public Field getField() {
        return field;
    }
}
