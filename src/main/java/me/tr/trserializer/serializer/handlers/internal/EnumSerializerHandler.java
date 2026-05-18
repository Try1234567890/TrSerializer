package me.tr.trserializer.serializer.handlers.internal;

import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.deserializer.handlers.internal.EnumDeserializerHandler;
import me.tr.trserializer.exceptions.TranslationError;
import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.serializer.SerializerTask;
import me.tr.trserializer.serializer.handlers.SerializerHandler;

public class EnumSerializerHandler implements SerializerHandler {
    public static final EnumSerializerHandler INSTANCE = new EnumSerializerHandler();

    @Override
    public void serialize(SerializerTask task) throws TranslationError, TypeMissMatched {
        if (!(task.getObject() instanceof Enum<?> enumerator)) return;

        // TODO: An options to choose which method call.
        //       with support for a custom one.
        task.getResult().accept(enumerator.name());
    }

    @Override
    public boolean canHandle(SerializerTask task) {
        Object obj = task.getObject();
        return obj instanceof Enum<?>;
    }

    @Override
    public DeserializerHandler getDeserializerHandler() {
        return EnumDeserializerHandler.INSTANCE;
    }
}
