package me.tr.trserializer.serializer.handlers.dates;

import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.exceptions.TranslationError;
import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.serializer.SerializerTask;
import me.tr.trserializer.serializer.handlers.SerializerHandler;

import java.sql.Date;

public class SQLDateHandler implements SerializerHandler {
    public static final SQLDateHandler INSTANCE = new SQLDateHandler();

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
        return null;
    }
}
