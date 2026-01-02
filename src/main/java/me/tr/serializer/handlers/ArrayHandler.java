package me.tr.serializer.handlers;

import me.tr.serializer.processes.Process;
import me.tr.serializer.processes.deserializer.Deserializer;
import me.tr.serializer.processes.serializer.Serializer;
import me.tr.serializer.types.GenericType;

import java.lang.reflect.Array;

public class ArrayHandler implements TypeHandler {
    private final Process process;

    public ArrayHandler(Process process) {
        this.process = process;
    }

    @Override
    public Object deserialize(Object obj, GenericType<?> type) {
        if (!obj.getClass().isArray() && !(obj instanceof java.util.Collection)) {
            throw new IllegalArgumentException("Expected array or collection for deserialization, got: " + obj.getClass());
        }

        Class<?> component = type.getFirstType();

        int length = Array.getLength(obj);

        Object resultArray = Array.newInstance(component, length);

        for (int i = 0; i < length; i++) {
            Object rawValue = Array.get(obj, i);
            Object value = getDeserializer().deserialize(rawValue, component);

            if (value != null || !component.isPrimitive()) {
                Array.set(resultArray, i, value);
            }
        }
        return resultArray;
    }

    @Override
    public Object serialize(Object obj, GenericType<?> type) {
        int length = Array.getLength(obj);

        Object result = Array.newInstance(Object.class, length);

        for (int i = 0; i < length; i++) {
            Object rawValue = Array.get(obj, i);

            Object value = getSerializer().serialize(rawValue);

            Array.set(result, i, value);
        }

        return result;
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