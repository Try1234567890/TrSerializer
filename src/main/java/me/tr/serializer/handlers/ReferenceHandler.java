package me.tr.serializer.handlers;

import me.tr.serializer.processes.Process;
import me.tr.serializer.processes.deserializer.Deserializer;
import me.tr.serializer.processes.serializer.Serializer;
import me.tr.serializer.types.GenericType;

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
    public Object deserialize(Object obj, GenericType<?> type) {
        return createReference(type.getClazz(), getDeserializer().deserialize(obj, type.getActualTypeArguments()[0]));
    }

    @Override
    public Object serialize(Object obj, GenericType<?> type) {
        if (obj instanceof Reference<?> ref)
            return getSerializer().serialize(ref.get());
        return null;
    }

    public Reference<?> createReference(Class<?> clazz, Object value) {
        if (value == null) return null;

        if (WeakReference.class.isAssignableFrom(clazz)) {
            return new WeakReference<>(value);
        } else if (SoftReference.class.isAssignableFrom(clazz)) {
            return new SoftReference<>(value);
        } else if (PhantomReference.class.isAssignableFrom(clazz)) {
            throw new UnsupportedOperationException("PhantomReference is not supported for automatic deserialization.");
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
