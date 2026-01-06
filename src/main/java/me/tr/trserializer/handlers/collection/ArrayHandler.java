package me.tr.trserializer.handlers.collection;

import me.tr.trserializer.handlers.TypeHandler;
import me.tr.trserializer.processes.process.Process;
import me.tr.trserializer.processes.deserializer.Deserializer;
import me.tr.trserializer.processes.serializer.Serializer;
import me.tr.trserializer.types.GenericType;

import java.lang.reflect.Array;

public class ArrayHandler implements TypeHandler {
    private final Process process;

    public ArrayHandler(Process process) {
        this.process = process;
    }

    @Override
    public Object deserialize(Object obj, GenericType<?> type) {
        Class<?> component = type.getFirstArgumentType();

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

        if (length == 0)
            return obj;


        boolean checked = false;
        Class<?> component = Object.class;
        Object result = null;


        for (int i = 0; i < length; i++) {
            Object rawValue = Array.get(obj, i);

            Object value = getSerializer().serialize(rawValue);

            if (!checked) {
                component = value == null ? Object.class : value.getClass();
                result = Array.newInstance(component, length);
                checked = true;
            }

            // means is a generic array, so result will be an Object[].
            if (value != null && !component.isAssignableFrom(value.getClass())) {
                result = convertToGenericArray(result);
            }


            Array.set(result, i, value);
        }

        return result;
    }

    private Object[] convertToGenericArray(Object array) {
        int len = Array.getLength(array);
        Object[] newArray = new Object[len];

        for (int i = 0; i < len; i++) {
            newArray[i] = Array.get(array, i);
        }

        return newArray;
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