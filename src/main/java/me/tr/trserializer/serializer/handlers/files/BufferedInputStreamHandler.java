package me.tr.trserializer.serializer.handlers.files;

import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.exceptions.HandlerError;
import me.tr.trserializer.exceptions.TranslationError;
import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.serializer.SerializerTask;
import me.tr.trserializer.serializer.handlers.SerializerHandler;

import java.io.BufferedInputStream;
import java.io.IOException;

public class BufferedInputStreamHandler implements SerializerHandler {
    public static final BufferedInputStreamHandler INSTANCE = new BufferedInputStreamHandler();

    @Override
    public void serialize(SerializerTask task) throws TranslationError, TypeMissMatched {
        if (!(task.getObject() instanceof BufferedInputStream bis)) return;

        try {
            bis.mark(0);
            bis.reset();
            byte[] bytes = bis.readAllBytes();
            task.getResult().accept(bytes);
        } catch (IOException e) {
            throw new HandlerError("An error occurs while reading the buffered input stream.", e);
        }
    }

    @Override
    public boolean canHandle(SerializerTask task) {
        Object obj = task.getObject();
        return obj instanceof BufferedInputStream;
    }

    @Override
    public DeserializerHandler getDeserializerHandler() {
        return null;
    }
}
