package me.tr.trserializer.serializer.handlers.dates;

import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.exceptions.TranslationError;
import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.serializer.SerializerTask;
import me.tr.trserializer.serializer.handlers.SerializerHandler;

import java.time.LocalDate;

public class LocalDateHandler implements SerializerHandler {
    public static final LocalDateHandler INSTANCE = new LocalDateHandler();

    @Override
    public void serialize(SerializerTask task) throws TranslationError, TypeMissMatched {
        if (!(task.getObject() instanceof LocalDate date)) return;

        task.getResult().accept(date.toString());
    }

    @Override
    public boolean canHandle(SerializerTask task) {
        Object obj = task.getObject();
        return obj instanceof LocalDate;
    }

    @Override
    public DeserializerHandler getDeserializerHandler() {
        return null;
    }
}
