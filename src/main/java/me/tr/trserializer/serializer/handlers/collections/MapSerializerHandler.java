package me.tr.trserializer.serializer.handlers.collections;

import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.exceptions.HandlerError;
import me.tr.trserializer.serializer.SerializerTask;
import me.tr.trserializer.serializer.handlers.SerializerHandler;
import me.tr.trserializer.types.GenericType;

import java.util.Map;

public class MapHandler implements SerializerHandler {
    public static final MapHandler INSTANCE = new MapHandler();

    @SuppressWarnings("unchecked")
    @Override
    public void serialize(SerializerTask task) {
        if (!(task.getObject() instanceof Map<?, ?> map)) return;
        GenericType<?> type = task.getGenericType();
        Map<Object, Object> result = task.getInstancer().instance(map.getClass());

        for (Map.Entry<?, ?> entry : map.entrySet()) {
            task.serialize(entry, new GenericType<>(Map.Entry.class, type.getArgumentClass(0), type.getArgumentClass(1)),
                    (o) -> {
                        Map.Entry<?, ?> subentry = (Map.Entry<?, ?>) o;
                        if (subentry == null)
                            throw new HandlerError("An error occurs while serializing Map.Entry: " + entry.getKey() + " - " + entry.getValue());

                        result.put(subentry.getKey(), subentry.getValue());
                    });
        }

        task.getResult().accept(result);
    }

    @Override
    public boolean canHandle(SerializerTask task) {
        Object obj = task.getObject();
        return obj instanceof Map<?, ?>;
    }

    @Override
    public DeserializerHandler getDeserializerHandler() {
        return me.tr.trserializer.deserializer.handlers.collections.MapHandler.INSTANCE;
    }
}
