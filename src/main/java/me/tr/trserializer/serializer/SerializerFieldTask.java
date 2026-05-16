package me.tr.trserializer.serializer;

import me.tr.trserializer.serializer.helpers.savable.SerializerTaskSavabilityChecker;
import me.tr.trserializer.translator.TranslatorFieldTask;
import me.tr.trserializer.types.GenericType;

import java.lang.reflect.Field;
import java.util.UUID;
import java.util.function.Consumer;

public abstract class SerializerFieldTask extends SerializerTask implements TranslatorFieldTask {
    private final Field field;

    public SerializerFieldTask(UUID id, Serializer<?> serializer, Object object, GenericType<?> type, Consumer<Object> result, SerializerTaskSavabilityChecker savabilityChecker, Field field) {
        super(id, serializer, object, type, result, savabilityChecker);
        this.field = field;
    }

    public SerializerFieldTask(Serializer<?> serializer, Object object, GenericType<?> type, Consumer<Object> result, SerializerTaskSavabilityChecker savabilityChecker, Field field) {
        super(serializer, object, type, result, savabilityChecker);
        this.field = field;
    }

    public SerializerFieldTask(UUID id, Serializer<?> serializer, Object object, GenericType<?> type, Consumer<Object> result, Field field) {
        super(id, serializer, object, type, result);
        this.field = field;
    }

    public SerializerFieldTask(Serializer<?> serializer, Object object, GenericType<?> type, Consumer<Object> result, Field field) {
        super(serializer, object, type, result);
        this.field = field;
    }

    public SerializerFieldTask(Serializer<?> serializer, Object object, Consumer<Object> result, Field field) {
        super(serializer, object, result);
        this.field = field;
    }

    @Override
    public Field getField() {
        return field;
    }
}
