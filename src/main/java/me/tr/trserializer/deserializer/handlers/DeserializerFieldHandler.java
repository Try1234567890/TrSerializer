package me.tr.trserializer.deserializer.handlers;

import me.tr.trserializer.deserializer.DeserializerFieldTask;
import me.tr.trserializer.serializer.handlers.SerializerFieldHandler;

public interface DeserializerFieldHandler {

    void deserialize(DeserializerFieldTask task);

    boolean canHandle(DeserializerFieldTask task);

    SerializerFieldHandler getSerializerHandler();

}
