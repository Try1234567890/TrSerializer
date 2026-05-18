package me.tr.trserializer.serializer.handlers.dates;

import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.deserializer.handlers.dates.DateDeserializerHandler;
import me.tr.trserializer.exceptions.TranslationError;
import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.serializer.SerializerTask;
import me.tr.trserializer.serializer.handlers.SerializerHandler;

import java.util.Date;

public class DateSerializerHandler implements SerializerHandler {
    public static final DateSerializerHandler INSTANCE = new DateSerializerHandler();

    @Override
    public void serialize(SerializerTask task) throws TranslationError, TypeMissMatched {
        if (!(task.getObject() instanceof Date date)) return;

        task.getResult().accept(date.toInstant().toEpochMilli());
    }

    @Override
    public boolean canHandle(SerializerTask task) {
        Object obj = task.getObject();
        return obj instanceof Date;
    }

    @Override
    public DeserializerHandler getDeserializerHandler() {
        return DateDeserializerHandler.INSTANCE;
    }
}
