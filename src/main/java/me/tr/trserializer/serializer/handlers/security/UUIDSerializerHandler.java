package me.tr.trserializer.serializer.handlers.security;

import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.exceptions.TranslationError;
import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.serializer.SerializerTask;
import me.tr.trserializer.serializer.handlers.SerializerHandler;

import java.util.UUID;

public class UUIDHandler implements SerializerHandler {
    public static final UUIDHandler INSTANCE = new UUIDHandler();

    @Override
    public void serialize(SerializerTask task) throws TranslationError, TypeMissMatched {
        if (!(task.getObject() instanceof UUID uuid)) return;

        task.getResult().accept(uuid.toString());
    }

    @Override
    public boolean canHandle(SerializerTask task) {
        Object obj = task.getObject();
        return obj instanceof UUID;
    }

    @Override
    public DeserializerHandler getDeserializerHandler() {
        return me.tr.trserializer.deserializer.handlers.security.UUIDHandler.INSTANCE;
    }
}
