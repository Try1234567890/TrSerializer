package me.tr.trserializer.serializer.handlers.containers;

import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.exceptions.TranslationError;
import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.serializer.SerializerTask;
import me.tr.trserializer.serializer.handlers.SerializerHandler;

import java.lang.reflect.Array;
import java.util.concurrent.atomic.*;

public class AtomicHandler implements SerializerHandler {
    public static final AtomicHandler INSTANCE = new AtomicHandler();

    @Override
    public void serialize(SerializerTask task) throws TranslationError, TypeMissMatched {
        Object obj = task.getObject();
        switch (obj) {
            case AtomicReference<?> ref -> task.serialize(ref.get());
            case AtomicReferenceArray<?> refArr -> task.getResult().accept(get(task, refArr));
            case AtomicInteger atomicInteger -> task.serialize(atomicInteger.get());
            case AtomicIntegerArray intArr -> task.getResult().accept(get(intArr));
            case AtomicLong atomicLong -> task.serialize(atomicLong.get());
            case AtomicLongArray longArr -> task.getResult().accept(get(longArr));
            case AtomicBoolean bool -> task.serialize(bool.get());
            default -> {
            }
        }
    }

    private Integer[] get(AtomicIntegerArray arr) {
        if (arr.length() == 0) return new Integer[0];
        Integer[] result = new Integer[arr.length()];

        for (int i = 0; i < arr.length(); i++) {
            result[i] = arr.get(i);
        }

        return result;
    }

    private Long[] get(AtomicLongArray arr) {
        if (arr.length() == 0) return new Long[0];
        Long[] result = new Long[arr.length()];

        for (int i = 0; i < arr.length(); i++) {
            result[i] = arr.get(i);
        }

        return result;
    }

    private Object[] get(SerializerTask task, AtomicReferenceArray<?> arr) {
        if (arr.length() == 0) return new Object[0];
        Object[] result = (Object[]) Array.newInstance(arr.get(0).getClass(), arr.length());

        for (int i = 0; i < arr.length(); i++) {
            int finalI = i;
            task.serialize(arr.get(i), (o) -> result[finalI] = o);
        }

        return result;
    }

    @Override
    public boolean canHandle(SerializerTask task) {
        Object obj = task.getObject();
        return obj instanceof AtomicReference<?> ||
                obj instanceof AtomicReferenceArray<?> ||
                obj instanceof AtomicInteger ||
                obj instanceof AtomicIntegerArray ||
                obj instanceof AtomicLong ||
                obj instanceof AtomicLongArray ||
                obj instanceof AtomicBoolean;
    }

    @Override
    public DeserializerHandler getDeserializerHandler() {
        return null;
    }
}
