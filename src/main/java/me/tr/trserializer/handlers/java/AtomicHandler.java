package me.tr.trserializer.handlers.java;

import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.handlers.TypeHandler;
import me.tr.trserializer.logger.TrLogger;
import me.tr.trserializer.processes.Process;
import me.tr.trserializer.processes.deserializer.Deserializer;
import me.tr.trserializer.processes.serializer.Serializer;
import me.tr.trserializer.types.GenericType;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class AtomicHandler implements TypeHandler {
    private final Process process;

    public AtomicHandler(Process process) {
        this.process = process;
    }


    @Override
    public Object deserialize(Object obj, GenericType<?> type) {
        Class<?> clazz = type.getTypeClass();

        Object innerValue = getDeserializer().deserialize(obj, type.getFirstArgumentType());

        return createAtomicReference(clazz, innerValue);
    }

    @Override
    public Object serialize(Object obj, GenericType<?> type) {
        return switch (obj) {
            case AtomicReference<?> ref -> getSerializer().serialize(ref.get());
            case AtomicInteger i -> getSerializer().serialize(i.get());
            case AtomicLong l -> getSerializer().serialize(l.get());
            case AtomicBoolean b -> getSerializer().serialize(b.get());
            case null, default -> null;
        };
    }

    public Object createAtomicReference(Class<?> clazz, Object value) {
        if (value == null) return null;

        if (AtomicInteger.class.isAssignableFrom(clazz)) {
            if (!(value instanceof Number num)) {
                TrLogger.getInstance().exception(
                        new TypeMissMatched("The provided value for AtomicInteger is not an number: " + value.getClass()));
                return null;
            }
            return new AtomicInteger(num.intValue());
        }

        if (AtomicLong.class.isAssignableFrom(clazz)) {
            if (!(value instanceof Number num)) {
                TrLogger.getInstance().exception(
                        new TypeMissMatched("The provided value for AtomicLong is not an number: " + value.getClass()));
                return null;
            }
            return new AtomicLong(num.longValue());
        }

        if (AtomicBoolean.class.isAssignableFrom(clazz)) {
            if (!(value instanceof Boolean bool)) {
                TrLogger.getInstance().exception(
                        new TypeMissMatched("The provided value for AtomicBoolean is not a boolean: " + value.getClass()));
                return null;
            }
            return new AtomicBoolean(bool);
        }

        if (AtomicReference.class.isAssignableFrom(clazz)) {
            return new AtomicReference<>(value);
        }

        return null;
    }

    public Process getProcess() {
        return process;
    }


    private Deserializer getDeserializer() {
        return (Deserializer) process;
    }

    private Serializer getSerializer() {
        return (Serializer) process;
    }
}
