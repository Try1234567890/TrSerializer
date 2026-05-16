package me.tr.trserializer.serializer.handlers;

import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.exceptions.TranslationError;
import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.serializer.SerializerTask;

public interface SerializerHandler {

    void serialize(SerializerTask task) throws TranslationError, TypeMissMatched;

    boolean canHandle(SerializerTask task);

    DeserializerHandler getDeserializerVersion();

}
