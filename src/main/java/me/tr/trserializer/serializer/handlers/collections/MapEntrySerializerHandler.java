package me.tr.trserializer.serializer.handlers.collections;

import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.deserializer.handlers.collections.MapEntryDeserializerHandler;
import me.tr.trserializer.serializer.SerializerTask;
import me.tr.trserializer.serializer.handlers.SerializerHandler;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class MapEntrySerializerHandler implements SerializerHandler {
    public static final MapEntrySerializerHandler INSTANCE = new MapEntrySerializerHandler();

    @SuppressWarnings("unchecked")
    @Override
    public void serialize(SerializerTask task) {
        if (!(task.getObject() instanceof Map.Entry<?, ?> entry)) return;

        CompletableFuture<Object> keyFuture = new CompletableFuture<>();
        CompletableFuture<Object> valueFuture = new CompletableFuture<>();

        keyFuture.thenAcceptBoth(valueFuture, (key, value) -> {
            Map.Entry<Object, Object> subentry = Map.entry(key, value);
            task.getResult().accept(subentry);
        });

        task.serialize(entry.getKey(), keyFuture::complete);
        task.serialize(entry.getValue(), valueFuture::complete);
    }

    @Override
    public boolean canHandle(SerializerTask task) {
        Object obj = task.getObject();
        return obj instanceof Map.Entry<?, ?>;
    }

    @Override
    public DeserializerHandler getDeserializerHandler() {
        return MapEntryDeserializerHandler.INSTANCE;
    }
}
