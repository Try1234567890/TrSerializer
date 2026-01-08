package me.tr.trserializer.handlers.annotation;

import me.tr.trserializer.handlers.TypeHandler;
import me.tr.trserializer.processes.deserializer.Deserializer;
import me.tr.trserializer.processes.process.Process;
import me.tr.trserializer.processes.serializer.Serializer;
import me.tr.trserializer.types.GenericType;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class UnwrappedHandler implements TypeHandler {
    private final Process process;
    private final Object instance;
    private final Set<Field> fields;


    public UnwrappedHandler(Process process, Set<Field> fields, Object instance) {
        this.process = process;
        this.fields = fields;
        this.instance = instance;
    }


    @SuppressWarnings("unchecked")
    @Override
    public Object deserialize(Object obj, GenericType<?> type) {
        for (Field field : getFields()) {
            getDeserializer().deserialize(field, instance, instance.getClass(), (Map<String, Object>) obj);
        }

        return instance;
    }

    @Override
    public Map<String, Object> serialize(Object instance, GenericType<?> type) {
        Map<String, Object> map = new HashMap<>();

        for (Field field : getFields()) {
            getSerializer().serialize(field, instance, instance.getClass(), map);
        }

        return map;
    }

    public Set<Field> getFields() {
        return fields;
    }

    public Process getProcess() {
        return process;
    }

    private Deserializer getDeserializer() {
        return (Deserializer) getProcess();
    }

    private Serializer getSerializer() {
        return (Serializer) getProcess();
    }
}
