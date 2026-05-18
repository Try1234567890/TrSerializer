package me.tr.trserializer.deserializer.handlers.files;

import me.tr.trserializer.deserializer.DeserializerTask;
import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.exceptions.HandlerError;
import me.tr.trserializer.serializer.handlers.SerializerHandler;
import me.tr.trserializer.serializer.handlers.files.URLSerializerHandler;
import me.tr.trserializer.types.GenericType;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class URLDeserializerHandler implements DeserializerHandler {
    public static final URLDeserializerHandler INSTANCE = new URLDeserializerHandler();

    @Override
    public void deserialize(DeserializerTask task) {
        if (!(task.getObject() instanceof String str)) return;

        try {
            URL url = URI.create(str).toURL();
            task.getResult().accept(url);
        } catch (MalformedURLException | IllegalArgumentException e) {
            throw new HandlerError("An error occurs while parsing URL: " + str, e);
        }
    }

    @Override
    public boolean canHandle(DeserializerTask task) {
        Object obj = task.getObject();
        GenericType<?> type = task.getGenericType();
        return type.is(URL.class) && obj instanceof String;
    }

    @Override
    public SerializerHandler getSerializerHandler() {
        return URLSerializerHandler.INSTANCE;
    }
}
