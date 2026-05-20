package me.tr.trserializer.serializer.handlers;

import me.tr.trserializer.deserializer.handlers.DeserializerFieldHandler;
import me.tr.trserializer.exceptions.TranslationError;
import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.serializer.SerializerFieldTask;

public interface SerializerFieldHandler {

    void serialize(SerializerFieldTask task) throws TranslationError, TypeMissMatched;

    boolean canHandle(SerializerFieldTask task);

    DeserializerFieldHandler getDeserializerHandler();

}
