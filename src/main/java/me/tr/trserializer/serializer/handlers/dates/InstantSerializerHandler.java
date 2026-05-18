package me.tr.trserializer.serializer.handlers.dates;

import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.deserializer.handlers.dates.InstantDeserializerHandler;
import me.tr.trserializer.exceptions.TranslationError;
import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.serializer.SerializerTask;
import me.tr.trserializer.serializer.handlers.SerializerHandler;

import java.time.Instant;

public class InstantSerializerHandler implements SerializerHandler {
    public static final InstantSerializerHandler INSTANCE = new InstantSerializerHandler();

    @Override
    public void serialize(SerializerTask task) throws TranslationError, TypeMissMatched {
        if (!(task.getObject() instanceof Instant instant)) return;

        task.getResult().accept(instant.toEpochMilli());
    }

    @Override
    public boolean canHandle(SerializerTask task) {
        Object obj = task.getObject();
        return obj instanceof Instant;
    }

    @Override
    public DeserializerHandler getDeserializerHandler() {
        return InstantDeserializerHandler.INSTANCE;
    }
}
