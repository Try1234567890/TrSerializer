package me.tr.trserializer.handlers.java;

import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.handlers.TypeHandler;
import me.tr.trserializer.processes.deserializer.Deserializer;
import me.tr.trserializer.processes.process.Process;
import me.tr.trserializer.processes.serializer.Serializer;
import me.tr.trserializer.types.GenericType;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

public class ReferenceHandler implements TypeHandler {
    private Process process;

    public ReferenceHandler(Process process) {
        this.process = process;
    }

    @Override
    public Reference<?> deserialize(Object obj, GenericType<?> type) {
        Object des = getDeserializer().deserialize(obj, type.getFirstArgumentType());

        return createReference(type.getTypeClass(), des);
    }

    @Override
    public Object serialize(Object obj, GenericType<?> type) {
        if (obj instanceof Reference<?> ref) {
            return getSerializer().serialize(ref.get());
        }

        getProcess().getLogger().throwable(
                new TypeMissMatched("The provided object " + obj + " is not convertible to an Reference"));

        return null;
    }

    public Reference<?> createReference(Class<?> clazz, Object value) {
        if (value == null) return null;

        if (WeakReference.class.isAssignableFrom(clazz)) {
            return new WeakReference<>(value);
        } else if (SoftReference.class.isAssignableFrom(clazz)) {
            return new SoftReference<>(value);
        } else if (PhantomReference.class.isAssignableFrom(clazz)) {
            getProcess().getLogger().throwable(
                    new UnsupportedOperationException("PhantomReference is not supported for automatic deserialization."));
        }

        return new SoftReference<>(value);
    }

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }


    private Deserializer getDeserializer() {
        return (Deserializer) process;
    }

    private Serializer getSerializer() {
        return (Serializer) process;
    }
}
