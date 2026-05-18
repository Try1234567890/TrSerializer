package me.tr.trserializer.deserializer.handlers.dates;

import me.tr.trserializer.deserializer.DeserializerTask;
import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.serializer.handlers.SerializerHandler;
import me.tr.trserializer.serializer.handlers.dates.DurationSerializerHandler;
import me.tr.trserializer.types.GenericType;

import java.time.Duration;

public class DurationDeserializerHandler implements DeserializerHandler {
    public static final DurationDeserializerHandler INSTANCE = new DurationDeserializerHandler();

    @Override
    public void deserialize(DeserializerTask task) {
        if (!(task.getObject() instanceof Long millis)) return;

        Duration duration = Duration.ofMillis(millis);
        task.getResult().accept(duration);
    }

    @Override
    public boolean canHandle(DeserializerTask task) {
        Object obj = task.getObject();
        GenericType<?> type = task.getGenericType();
        return type.is(Duration.class) && obj instanceof Long;
    }

    @Override
    public SerializerHandler getSerializerHandler() {
        return DurationSerializerHandler.INSTANCE;
    }
}
