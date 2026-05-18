package me.tr.trserializer.deserializer.handlers.dates;

import me.tr.trserializer.deserializer.DeserializerTask;
import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.serializer.handlers.SerializerHandler;
import me.tr.trserializer.serializer.handlers.dates.InstantSerializerHandler;
import me.tr.trserializer.types.GenericType;

import java.time.Instant;

public class InstantDeserializerHandler implements DeserializerHandler {
    public static final InstantDeserializerHandler INSTANCE = new InstantDeserializerHandler();

    @Override
    public void deserialize(DeserializerTask task) {
        if (!(task.getObject() instanceof Long millis)) return;

        Instant instant = Instant.ofEpochMilli(millis);
        task.getResult().accept(instant);
    }

    @Override
    public boolean canHandle(DeserializerTask task) {
        Object obj = task.getObject();
        GenericType<?> type = task.getGenericType();
        return type.is(Instant.class) && obj instanceof Long;
    }

    @Override
    public SerializerHandler getSerializerHandler() {
        return InstantSerializerHandler.INSTANCE;
    }
}
