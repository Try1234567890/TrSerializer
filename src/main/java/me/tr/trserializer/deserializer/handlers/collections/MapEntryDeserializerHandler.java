package me.tr.trserializer.deserializer.handlers.collections;

import me.tr.trserializer.deserializer.DeserializerTask;
import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.serializer.handlers.SerializerHandler;
import me.tr.trserializer.serializer.handlers.collections.MapEntrySerializerHandler;
import me.tr.trserializer.types.GenericType;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class MapEntryDeserializerHandler implements DeserializerHandler {
    public static final MapEntryDeserializerHandler INSTANCE = new MapEntryDeserializerHandler();

    @Override
    public void deserialize(DeserializerTask task) {
        if (!(task.getObject() instanceof Map.Entry<?, ?> entry)) return;

        GenericType<?> type = task.getGenericType();
        CompletableFuture<Object> keyFuture = new CompletableFuture<>();
        CompletableFuture<Object> valueFuture = new CompletableFuture<>();

        keyFuture.thenAcceptBoth(valueFuture, (key, value) -> task.getResult().accept(Map.entry(key, value)));

        task.deserialize(entry.getKey(), new GenericType<>(type.getArgumentClass(0)), keyFuture::complete);
        task.deserialize(entry.getValue(), new GenericType<>(type.getArgumentClass(1)), valueFuture::complete);
    }

    @Override
    public boolean canHandle(DeserializerTask task) {
        Object obj = task.getObject();
        GenericType<?> type = task.getGenericType();

        return type.is(Map.Entry.class) && obj instanceof Map.Entry<?, ?>;
    }

    @Override
    public SerializerHandler getSerializerHandler() {
        return MapEntrySerializerHandler.INSTANCE;
    }
}
