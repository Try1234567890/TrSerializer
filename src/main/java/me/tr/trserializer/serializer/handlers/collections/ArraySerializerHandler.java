package me.tr.trserializer.serializer.handlers.collections;

import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.exceptions.TranslationError;
import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.serializer.SerializerTask;
import me.tr.trserializer.serializer.handlers.SerializerHandler;

import java.lang.reflect.Array;

public class ArrayHandler implements SerializerHandler {
    public static final ArrayHandler INSTANCE = new ArrayHandler();

    @Override
    public void serialize(SerializerTask task) throws TranslationError, TypeMissMatched {
        Object obj = task.getObject();
        int length = Array.getLength(obj);
        if (length == 0) return;

        Object result = Array.newInstance(task.getGenericType().getFirstArgumentClass(), length);

        for (int i = 0; i < length; i++) {
            int finalI = i;
            Object rawValue = Array.get(obj, i);
            task.serialize(rawValue, (o) -> Array.set(result, finalI, o));
        }

        task.getResult().accept(result);
    }

    @Override
    public boolean canHandle(SerializerTask task) {
        Object obj = task.getObject();
        return obj != null && obj.getClass().isArray();
    }

    @Override
    public DeserializerHandler getDeserializerHandler() {
        return me.tr.trserializer.deserializer.handlers.collections.ArrayHandler.INSTANCE;
    }
}