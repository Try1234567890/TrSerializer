package me.tr.trserializer.serializer.handlers.collections;

import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.deserializer.handlers.collections.ArrayDeserializerHandler;
import me.tr.trserializer.exceptions.TranslationError;
import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.serializer.SerializerTask;
import me.tr.trserializer.serializer.handlers.SerializerHandler;
import me.tr.trserializer.utility.Wrappers;

import java.lang.reflect.Array;

public class ArraySerializerHandler implements SerializerHandler {
    public static final ArraySerializerHandler INSTANCE = new ArraySerializerHandler();

    @Override
    public void serialize(SerializerTask task) throws TranslationError, TypeMissMatched {
        if (task.getObject() == null || !task.getObject().getClass().isArray()) return;
        Object arr = task.getObject();
        int length = Array.getLength(arr);
        if (length == 0) return;

        Class<?> expectedClass = task.getGenericType().getFirstArgumentClass();
        Object resultArray = Array.newInstance(expectedClass, length);

        for (int i = 0; i < length; i++) {
            final int finalI = i;
            Object rawValue = Array.get(arr, finalI);
            task.serialize(rawValue, (o) -> set(task, resultArray, finalI, o, expectedClass));
        }

        task.getResult().accept(resultArray);
    }

    private void set(SerializerTask task, Object array, int index, Object result, Class<?> expectedClass) {
        if (result == null) {
            // TODO: Adding option to remove null value.
            Array.set(array, index, null);
            return;
        }

        if (Wrappers.isAssignable(result.getClass(), expectedClass)) {
            Array.set(array, index, result);
            return;
        }

        task.serialize(result, (o) -> set(task, array, index, o, expectedClass));
    }

    @Override
    public boolean canHandle(SerializerTask task) {
        Object obj = task.getObject();
        return obj != null && obj.getClass().isArray();
    }

    @Override
    public DeserializerHandler getDeserializerHandler() {
        return ArrayDeserializerHandler.INSTANCE;
    }
}