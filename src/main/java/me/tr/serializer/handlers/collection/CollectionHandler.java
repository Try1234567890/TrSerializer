package me.tr.serializer.handlers.collection;

import me.tr.serializer.handlers.TypeHandler;
import me.tr.serializer.processes.Process;
import me.tr.serializer.processes.deserializer.Deserializer;
import me.tr.serializer.processes.serializer.Serializer;
import me.tr.serializer.types.GenericType;

import java.lang.reflect.Array;
import java.util.*;

public class CollectionHandler implements TypeHandler {
    private final Process process;

    public CollectionHandler(Process process) {
        this.process = process;
    }


    @Override
    public Object deserialize(Object obj, GenericType<?> type) {
        Collection<?> source;
        if (obj instanceof Collection) {
            source = (Collection<?>) obj;
        } else if (obj.getClass().isArray()) {
            List<Object> temp = new ArrayList<>();
            int len = Array.getLength(obj);
            for (int i = 0; i < len; i++)
                temp.add(Array.get(obj, i));
            source = temp;
        } else {
            throw new IllegalArgumentException("Unsupported source for CollectionHandler: " + obj.getClass());
        }

        Collection<Object> result = createCollectionInstance(type.getTypeClass());

        for (Object item : source) {
            Object value = getDeserializer().deserialize(item, type.getFirstArgumentType());
            result.add(value);
        }

        return result;
    }

    @Override
    public Object serialize(Object obj, GenericType<?> type) {
        List<Object> result = new ArrayList<>();
        if (obj instanceof Collection<?> coll) {
            for (Object item : coll) {
                Object serializedItem = getSerializer().serialize(item);
                result.add(serializedItem);
            }
        }

        return result;
    }

    private Collection<Object> createCollectionInstance(Class<?> type) {
        if (type.isInterface()) {
            if (Set.class.isAssignableFrom(type)) return new HashSet<>();
            if (Queue.class.isAssignableFrom(type)) return new LinkedList<>();
            if (List.class.isAssignableFrom(type)) return new ArrayList<>();
        }
        return new ArrayList<>();
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