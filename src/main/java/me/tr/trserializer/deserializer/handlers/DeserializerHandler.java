package me.tr.trserializer.deserializer.handlers;

import me.tr.trserializer.deserializer.DeserializerTask;
import me.tr.trserializer.serializer.handlers.SerializerHandler;

public interface DeserializerHandler {

    void deserialize(DeserializerTask task);

    boolean canHandle(DeserializerTask task);

    SerializerHandler getSerializerHandler();

}
