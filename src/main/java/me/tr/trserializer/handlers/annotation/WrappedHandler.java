package me.tr.trserializer.handlers.annotation;

import me.tr.trserializer.handlers.TypeHandler;
import me.tr.trserializer.processes.deserializer.Deserializer;
import me.tr.trserializer.processes.process.Process;
import me.tr.trserializer.processes.serializer.Serializer;
import me.tr.trserializer.types.GenericType;
import me.tr.trserializer.utility.Utility;

import java.lang.reflect.Field;
import java.util.*;

public class WrappedHandler implements TypeHandler {
    private final Process process;
    private final String key;
    private final Object instance;
    private final Set<Field> fields;


    public WrappedHandler(Process process, String key, Set<Field> fields, Object instance) {
        this.process = process;
        this.key = key;
        this.fields = fields;
        this.instance = instance;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object deserialize(Object obj, GenericType<?> type) {
        if (!Utility.isAMapWithStringKeys(obj))
            return null;

        for (Field field : getFields()) {
            // TODO: FIX
            // Object object = getDeserializer().getMapValue(field, (Map<String, Object>) obj);

            //if (object != null) {
            //    return getDeserializer().deserialize(object, type);
            //}
        }

        return instance;
    }

    @Override
    public Map<String, Object> serialize(Object instance2, GenericType<?> type) {

        Object obj = getSerializer().serialize(instance, type);

        return new HashMap<>(Map.of(getKey(), obj));
    }

    public String getKey() {
        return key;
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
