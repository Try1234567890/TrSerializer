package me.tr.trserializer.handlers.collection;

import me.tr.trserializer.handlers.TypeHandler;
import me.tr.trserializer.logger.TrLogger;
import me.tr.trserializer.processes.process.Process;
import me.tr.trserializer.processes.deserializer.Deserializer;
import me.tr.trserializer.processes.serializer.Serializer;
import me.tr.trserializer.types.GenericType;

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
            for (Field field : getProcess().getFields(type.getTypeClass())) {
                try {
                    field.setAccessible(true);

                    Object value = field.get(obj);

                    if (value == null)
                        continue;

                    Object des = getDeserializer().deserialize(value, value.getClass());

                    result.put(field.getName(), des);
                } catch (IllegalAccessException e) {
                    TrLogger.exception(
                            new RuntimeException("An error occurs while accessing the field " + field.getName()));
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
