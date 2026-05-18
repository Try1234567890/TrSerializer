package me.tr.trserializer.serializer.handlers.containers;

import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.deserializer.handlers.containers.OptionalDeserializerHandler;
import me.tr.trserializer.exceptions.TranslationError;
import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.serializer.SerializerTask;
import me.tr.trserializer.serializer.handlers.SerializerHandler;
import me.tr.trserializer.utility.SLogger;

import java.util.Optional;

public class OptionalSerializerHandler implements SerializerHandler {
    public static final OptionalSerializerHandler INSTANCE = new OptionalSerializerHandler();

    @Override
    public void serialize(SerializerTask task) throws TranslationError, TypeMissMatched {
        if (!(task.getObject() instanceof Optional<?> optional)) return;

        if (optional.isEmpty()) {
            // TODO: Add option to return null if empty
            SLogger.LOGGER.warn("The Optional container is empty. Stopping...");
            return;
        }

        task.serialize(optional.get());
    }

    @Override
    public boolean canHandle(SerializerTask task) {
        Object obj = task.getObject();
        return obj instanceof Optional<?>;
    }

    @Override
    public DeserializerHandler getDeserializerHandler() {
        return OptionalDeserializerHandler.INSTANCE;
    }
}
