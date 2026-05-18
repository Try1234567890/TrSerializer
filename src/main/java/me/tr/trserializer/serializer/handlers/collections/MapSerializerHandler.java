package me.tr.trserializer.serializer.handlers.collections;

import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.deserializer.handlers.collections.MapDeserializerHandler;
import me.tr.trserializer.exceptions.HandlerError;
import me.tr.trserializer.exceptions.InstancerError;
import me.tr.trserializer.instancer.TranslatorInstancer;
import me.tr.trserializer.serializer.SerializerTask;
import me.tr.trserializer.serializer.handlers.SerializerHandler;
import me.tr.trserializer.types.GenericType;

import java.util.HashMap;
import java.util.Map;

public class MapSerializerHandler implements SerializerHandler {
    public static final MapSerializerHandler INSTANCE = new MapSerializerHandler();

    @Override
    public void serialize(SerializerTask task) {
        if (!(task.getObject() instanceof Map<?, ?> map)) return;
        GenericType<?> type = task.getGenericType();
        Map<Object, Object> result = getMap(map, task.getInstancer());

        for (Map.Entry<?, ?> entry : map.entrySet()) {
            task.serialize(entry, new GenericType<>(Map.Entry.class, type.getArgumentClass(0), type.getArgumentClass(1)),
                    (o) -> {
                        Map.Entry<?, ?> subentry = (Map.Entry<?, ?>) o;
                        if (subentry == null) {
                            throw new HandlerError("An error occurs while serializing Map.Entry: " + entry.getKey() + " - " + entry.getValue());
                        }
                        result.put(subentry.getKey(), subentry.getValue());
                    });
        }

        task.getResult().accept(result);
    }

    @SuppressWarnings("unchecked")
    private Map<Object, Object> getMap(Map<?, ?> map, TranslatorInstancer instancer) {
        try {
            return instancer.instance(map.getClass());
        } catch (InstancerError ex) {
            return new HashMap<>();
        }
    }

    @Override
    public boolean canHandle(SerializerTask task) {
        Object obj = task.getObject();
        return obj instanceof Map<?, ?>;
    }

    @Override
    public DeserializerHandler getDeserializerHandler() {
        return MapDeserializerHandler.INSTANCE;
    }
}
