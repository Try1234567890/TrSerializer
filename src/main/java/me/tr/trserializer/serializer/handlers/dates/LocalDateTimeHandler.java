package me.tr.trserializer.serializer.handlers.dates;

import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.exceptions.TranslationError;
import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.serializer.SerializerTask;
import me.tr.trserializer.serializer.handlers.SerializerHandler;

import java.time.LocalDateTime;

public class LocalDateTimeHandler implements SerializerHandler {
    public static final LocalDateTimeHandler INSTANCE = new LocalDateTimeHandler();

    @Override
    public void serialize(SerializerTask task) throws TranslationError, TypeMissMatched {
        if (!(task.getObject() instanceof LocalDateTime localDateTime)) return;

        task.getResult().accept(localDateTime.toString());
    }

    @Override
    public boolean canHandle(SerializerTask task) {
        Object obj = task.getObject();
        return obj instanceof LocalDateTime;
    }

    @Override
    public DeserializerHandler getDeserializerHandler() {
        return null;
    }
}
