package me.tr.serializer.handlers;

import me.tr.serializer.processes.Process;
import me.tr.serializer.processes.deserializer.Deserializer;
import me.tr.serializer.processes.serializer.Serializer;
import me.tr.serializer.types.GenericType;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class MapHandler implements TypeHandler {
    private final Process process;

    public MapHandler(Process process) {
        this.process = process;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<Object, Object> deserialize(Object obj, GenericType<?> type) {
        if (obj instanceof Map)
            return (Map<Object, Object>) obj;
        else {
            Map<Object, Object> result = new HashMap<>();
            for (Field field : type.getClazz().getDeclaredFields()) {
                try {
                    field.setAccessible(true);

                    Object value = field.get(obj);

                    value = getDeserializer().deserialize(value, value.getClass());

                    result.put(field.getName(), value);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("An error occurs while accessing the field " + field.getName());
                }
            }
            return result;
        }
    }

    @Override
    public Object serialize(Object obj, GenericType<?> type) {
        return obj;
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
