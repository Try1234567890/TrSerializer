package me.tr.trserializer.serializer.handlers.dates;

import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.exceptions.TranslationError;
import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.serializer.SerializerTask;
import me.tr.trserializer.serializer.handlers.SerializerHandler;

import java.time.OffsetDateTime;

public class OffsetDateTimeHandler implements SerializerHandler {
    public static final OffsetDateTimeHandler INSTANCE = new OffsetDateTimeHandler();

    @Override
    public void serialize(SerializerTask task) throws TranslationError, TypeMissMatched {
        if (!(task.getObject() instanceof OffsetDateTime offsetDateTime)) return;

        task.getResult().accept(offsetDateTime.toString());
    }

    @Override
    public boolean canHandle(SerializerTask task) {
        Object obj = task.getObject();
        return obj instanceof OffsetDateTime;
    }

    @Override
    public DeserializerHandler getDeserializerHandler() {
        return null;
    }
}
