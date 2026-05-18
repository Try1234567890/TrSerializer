package me.tr.trserializer.deserializer.handlers.collections;

import me.tr.trserializer.deserializer.DeserializerTask;
import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.exceptions.TranslationError;
import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.serializer.handlers.SerializerHandler;
import me.tr.trserializer.serializer.handlers.collections.ArraySerializerHandler;
import me.tr.trserializer.types.GenericType;
import me.tr.trserializer.utility.Wrappers;

import java.lang.reflect.Array;

public class ArrayDeserializerHandler implements DeserializerHandler {
    public static final ArrayDeserializerHandler INSTANCE = new ArrayDeserializerHandler();

    @Override
    public void deserialize(DeserializerTask task) throws TranslationError, TypeMissMatched {
        if (task.getObject() == null || !task.getObject().getClass().isArray()) return;

        Object arr = task.getObject(); // Raw Array (Savable Values)
        int length = Array.getLength(arr);
        GenericType<?> expectedType = new GenericType<>(task.getGenericType().getFirstArgumentClass());
        Class<?> expectedClass = expectedType.getTypeClass();

        Object result = Array.newInstance(expectedClass, length);

        for (int i = 0; i < length; i++) {
            int finalI = i;
            Object rawObject = Array.get(arr, finalI);
            task.deserialize(rawObject, expectedType, o -> set(task, result, finalI, o, expectedType));
        }

        task.getResult().accept(result);
    }

    private void set(DeserializerTask task, Object array, int index, Object result, GenericType<?> expectedType) {
        if (result == null) {
            Array.set(array, index, null);
            return;
        }

        if (Wrappers.isAssignable(result.getClass(), expectedType.getTypeClass())) {
            Array.set(array, index, result);
            return;
        }

        task.deserialize(result, expectedType, o -> set(task, array, index, o, expectedType));
    }

    @Override
    public boolean canHandle(DeserializerTask task) {
        Object obj = task.getObject();
        GenericType<?> type = task.getGenericType();
        return type.getTypeClass().isArray() && (obj != null && obj.getClass().isArray());
    }

    @Override
    public SerializerHandler getSerializerHandler() {
        return ArraySerializerHandler.INSTANCE;
    }
}