package me.tr.trserializer.serializer.handlers.files;

import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.deserializer.handlers.files.URIDeserializerHandler;
import me.tr.trserializer.exceptions.TranslationError;
import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.serializer.SerializerTask;
import me.tr.trserializer.serializer.handlers.SerializerHandler;

import java.net.URI;

public class URISerializerHandler implements SerializerHandler {
    public static final URISerializerHandler INSTANCE = new URISerializerHandler();

    @Override
    public void serialize(SerializerTask task) throws TranslationError, TypeMissMatched {
        if (!(task.getObject() instanceof URI uri)) return;

        task.getResult().accept(uri.normalize().toString());
    }

    @Override
    public boolean canHandle(SerializerTask task) {
        Object obj = task.getObject();
        return obj instanceof URI;
    }

    @Override
    public DeserializerHandler getDeserializerHandler() {
        return URIDeserializerHandler.INSTANCE;
    }
}
