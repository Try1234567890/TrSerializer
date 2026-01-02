package me.tr.serializer.handlers;

import me.tr.serializer.processes.Process;
import me.tr.serializer.types.GenericType;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class AtomicHandler implements TypeHandler {
    private Process process;

    public AtomicHandler(Process process) {
        this.process = process;
    }


    @Override
    public Object deserialize(Object obj, GenericType<?> type) {
        Class<?> clazz = type.getClazz();

        if (clazz.equals(AtomicReference.class)) {
            Object innerValue = getProcess().process(obj, type.getActualTypeArguments()[0]);
            return new AtomicReference<>(innerValue);
        }

        return createAtomicReference(clazz, obj);
    }

    @Override
    public Object serialize(Object obj, GenericType<?> type) {
        return switch (obj) {
            case AtomicReference<?> ref -> getProcess().process(ref.get(), type);
            case AtomicInteger i -> getProcess().process(i.get(), type);
            case AtomicLong l -> getProcess().process(l.get(), type);
            case AtomicBoolean b -> getProcess().process(b.get(), type);
            case null, default -> null;
        };
    }

    public Object createAtomicReference(Class<?> clazz, Object value) {
        if (value == null) return null;

        if (clazz.equals(AtomicInteger.class)) {
            return new AtomicInteger(value instanceof Number n ? n.intValue() : Integer.parseInt(value.toString()));
        }

        if (clazz.equals(AtomicLong.class)) {
            return new AtomicLong(value instanceof Number n ? n.longValue() : Long.parseLong(value.toString()));
        }

        if (clazz.equals(AtomicBoolean.class)) {
            return new AtomicBoolean(value instanceof Boolean b ? b : Boolean.parseBoolean(value.toString()));
        }

        if (clazz.equals(AtomicReference.class)) {
            return new AtomicReference<>(value);
        }

        return null;
    }

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }
}
