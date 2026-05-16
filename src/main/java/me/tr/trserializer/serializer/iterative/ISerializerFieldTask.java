package me.tr.trserializer.serializer.iterative;

import me.tr.trserializer.serializer.SerializerTask;
import me.tr.trserializer.serializer.helpers.savable.SerializerTaskSavabilityChecker;
import me.tr.trserializer.translator.TranslatorFieldTask;
import me.tr.trserializer.types.GenericType;

import java.lang.reflect.Field;
import java.util.Stack;
import java.util.UUID;
import java.util.function.Consumer;

public class ISerializerFieldTask extends ISerializerTask implements TranslatorFieldTask {
    private final Field field;

    public ISerializerFieldTask(UUID id, ISerializer serializer, Object object, GenericType<?> type, Consumer<Object> result,
                                SerializerTaskSavabilityChecker savabilityChecker, Stack<SerializerTask> tasks, Field field) {
        super(id, serializer, object, type, result, savabilityChecker, tasks);
        this.field = field;
    }

    public ISerializerFieldTask(ISerializer serializer, Object object, GenericType<?> type, Consumer<Object> result,
                                SerializerTaskSavabilityChecker savabilityChecker, Stack<SerializerTask> tasks, Field field) {
        super(serializer, object, type, result, savabilityChecker, tasks);
        this.field = field;
    }

    public ISerializerFieldTask(UUID id, ISerializer serializer, Object object, GenericType<?> type, Consumer<Object> result,
                                Stack<SerializerTask> tasks, Field field) {
        super(id, serializer, object, type, result, tasks);
        this.field = field;
    }

    public ISerializerFieldTask(ISerializer serializer, Object object, GenericType<?> type, Consumer<Object> result,
                                Stack<SerializerTask> tasks, Field field) {
        super(serializer, object, type, result, tasks);
        this.field = field;
    }

    public ISerializerFieldTask(ISerializer serializer, Object object, Consumer<Object> result, Stack<SerializerTask> tasks, Field field) {
        super(serializer, object, result, tasks);
        this.field = field;
    }

    @Override
    public Field getField() {
        return field;
    }
}
