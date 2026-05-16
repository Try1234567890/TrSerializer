package me.tr.trserializer.serializer.handlers.collections;

import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.serializer.SerializerTask;
import me.tr.trserializer.serializer.handlers.SerializerHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class MapHandler implements SerializerHandler {
    public static final MapHandler INSTANCE = new MapHandler();

    @Override
    public void serialize(SerializerTask task) {
        if (!(task.getObject() instanceof Map<?, ?> map)) return;
        // TODO: Add clone(Map) methods when instancer is finished.
        //       Clone the initial map for coherence.
        Map<Object, Object> result = new HashMap<>();

        for (Map.Entry<?, ?> entry : map.entrySet()) {
            CompletableFuture<Object> keyFuture = new CompletableFuture<>();
            CompletableFuture<Object> valueFuture = new CompletableFuture<>();

            keyFuture.thenAcceptBoth(valueFuture, result::put);
            task.serialize(entry.getKey(), keyFuture::complete);
            task.serialize(entry.getValue(), valueFuture::complete);
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
        return null;
    }
}
