package me.tr.trserializer.serializer.handlers.files;

import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.exceptions.HandlerError;
import me.tr.trserializer.exceptions.TranslationError;
import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.serializer.SerializerTask;
import me.tr.trserializer.serializer.handlers.SerializerHandler;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * This handler serialize an InputStream as an array of byte.
 * Obviously if the input stream has been read, the read bytes
 * will be ignored.
 * To avoid this issue {@link BufferedInputStreamHandler} can be used.
 * <p>
 * Also, the serialization of InputStreams can be disabled via annotation
 * or serializer options, in this case the handler respectively:
 * ignore the InputStream or thrown an {@link HandlerError}.
 */
public class InputStreamHandler implements SerializerHandler {
    public static final InputStreamHandler INSTANCE = new InputStreamHandler();

    @Override
    public void serialize(SerializerTask task) throws TranslationError, TypeMissMatched {
        if (!(task.getObject() instanceof InputStream is)) return;

        try {
            byte[] bytes = is.readAllBytes();
            task.getResult().accept(bytes);
        } catch (IOException e) {
            throw new HandlerError("An error occurs while reading the input stream.", e);
        }
    }

    @Override
    public boolean canHandle(SerializerTask task) {
        Object obj = task.getObject();
        return obj instanceof InputStream && !(obj instanceof BufferedInputStream);
    }

    @Override
    public DeserializerHandler getDeserializerHandler() {
        return me.tr.trserializer.deserializer.handlers.files.InputStreamHandler.INSTANCE;
    }
}
