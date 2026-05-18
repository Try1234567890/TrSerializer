package me.tr.trserializer.deserializer.handlers.containers;

import me.tr.trserializer.deserializer.DeserializerTask;
import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.exceptions.TranslationError;
import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.serializer.handlers.SerializerHandler;
import me.tr.trserializer.serializer.handlers.containers.AtomicSerializerHandler;
import me.tr.trserializer.types.GenericType;
import me.tr.trserializer.utility.Wrappers;

import java.lang.reflect.Array;
import java.util.Map;
import java.util.concurrent.atomic.*;

public class AtomicDeserializerHandler implements DeserializerHandler {
    public static final AtomicDeserializerHandler INSTANCE = new AtomicDeserializerHandler();

    @Override
    public void deserialize(DeserializerTask task) throws TranslationError, TypeMissMatched {
        Class<?> cls = task.getGenericType().getTypeClass();

        Object obj = task.getObject();
        Class<?> expectedObjectType = getExpectedClass(task.getGenericType());

        if (obj == null) {
            Object reference = task.instance();
            task.getResult().accept(reference);
            return;
        }

        if (Wrappers.isAssignable(obj.getClass(), getRepresentedClass(obj))) {
            Object reference = task.getInstancer().instance(cls, Map.of("initialValue", obj));
            task.getResult().accept(reference);
            return;
        }

        task.deserialize(obj, new GenericType<>(expectedObjectType), (o) -> task.deserialize(o, task.getGenericType(), task.getResult()));
    }

    private Class<?> getExpectedClass(GenericType<?> type) {
        Class<?> rawType = type.getTypeClass();
        return Wrappers.isAssignable(rawType, AtomicReference.class, AtomicReferenceArray.class) ? type.getFirstArgumentClass() : rawType;
    }

    private Class<?> getRepresentedClass(Object object) {
        if (object instanceof AtomicReference<?> ref) return ref.get().getClass();
        if (object instanceof AtomicReferenceArray<?> ref) return Array.newInstance(ref.get(0).getClass(), 0).getClass();
        if (object instanceof AtomicInteger) return Integer.class;
        if (object instanceof AtomicIntegerArray) return Integer[].class;
        if (object instanceof AtomicLong) return Long.class;
        if (object instanceof AtomicLongArray) return Long[].class;
        if (object instanceof AtomicBoolean) return Boolean.class;
        return object.getClass();
    }

    @Override
    public boolean canHandle(DeserializerTask task) {
        GenericType<?> type = task.getGenericType();

        return type.is(AtomicReference.class) ||
                type.is(AtomicReferenceArray.class) ||
                type.is(AtomicInteger.class) ||
                type.is(AtomicIntegerArray.class) ||
                type.is(AtomicLong.class) ||
                type.is(AtomicLongArray.class) ||
                type.is(AtomicBoolean.class);
    }

    @Override
    public SerializerHandler getSerializerHandler() {
        return AtomicSerializerHandler.INSTANCE;
    }

}
