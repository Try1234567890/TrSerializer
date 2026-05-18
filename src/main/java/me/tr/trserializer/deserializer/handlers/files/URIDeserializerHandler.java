package me.tr.trserializer.deserializer.handlers.files;

import me.tr.trserializer.deserializer.DeserializerTask;
import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.exceptions.HandlerError;
import me.tr.trserializer.serializer.handlers.SerializerHandler;
import me.tr.trserializer.serializer.handlers.files.URISerializerHandler;
import me.tr.trserializer.types.GenericType;

import java.net.URI;

public class URIDeserializerHandler implements DeserializerHandler {
    public static final URIDeserializerHandler INSTANCE = new URIDeserializerHandler();

    @Override
    public void deserialize(DeserializerTask task) {
        if (!(task.getObject() instanceof String str)) return;

        try {
            URI uri = URI.create(str);
            task.getResult().accept(uri);
        } catch (IllegalArgumentException e) {
            throw new HandlerError("An error occurs while parsing URI: " + str, e);
        }
    }

    @Override
    public boolean canHandle(DeserializerTask task) {
        Object obj = task.getObject();
        GenericType<?> type = task.getGenericType();
        return type.is(URI.class) && obj instanceof String;
    }

    @Override
    public SerializerHandler getSerializerHandler() {
        return URISerializerHandler.INSTANCE;
    }
}
