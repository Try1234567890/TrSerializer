package me.tr.trserializer.serializer.handlers.dates;

import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.exceptions.TranslationError;
import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.serializer.SerializerTask;
import me.tr.trserializer.serializer.handlers.SerializerHandler;

import java.time.ZonedDateTime;

public class ZonedDateTimeHandler implements SerializerHandler {
    public static final  ZonedDateTimeHandler INSTANCE = new ZonedDateTimeHandler();

    @Override
    public void serialize(SerializerTask task) throws TranslationError, TypeMissMatched {
        if (!(task.getObject() instanceof ZonedDateTime zonedDateTime)) return;

        task.getResult().accept(zonedDateTime.toInstant().toEpochMilli());
    }

    @Override
    public boolean canHandle(SerializerTask task) {
        Object obj = task.getObject();
        return obj instanceof ZonedDateTime;
    }

    @Override
    public DeserializerHandler getDeserializerHandler() {
        return null;
    }
}
