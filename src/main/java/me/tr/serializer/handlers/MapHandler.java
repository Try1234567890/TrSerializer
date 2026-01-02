package me.tr.serializer.handlers;

import me.tr.serializer.processes.Process;
import me.tr.serializer.types.GenericType;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class MapHandler implements TypeHandler {
    private Process process;

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

                    value = getProcess().process(value, value.getClass());

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

    public void setProcess(Process process) {
        this.process = process;
    }
}
