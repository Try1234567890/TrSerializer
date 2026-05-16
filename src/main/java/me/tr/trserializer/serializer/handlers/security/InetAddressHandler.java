package me.tr.trserializer.serializer.handlers.security;

import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.exceptions.TranslationError;
import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.serializer.SerializerTask;
import me.tr.trserializer.serializer.handlers.SerializerHandler;

import java.net.InetAddress;

public class InetAddressHandler implements SerializerHandler {
    public static final InetAddressHandler INSTANCE = new InetAddressHandler();

    @Override
    public void serialize(SerializerTask task) throws TranslationError, TypeMissMatched {
        if (!(task.getObject() instanceof InetAddress inetAddress)) return;

        task.getResult().accept(inetAddress.toString());
    }

    @Override
    public boolean canHandle(SerializerTask task) {
        Object obj = task.getObject();
        return obj instanceof InetAddress;
    }

    @Override
    public DeserializerHandler getDeserializerHandler() {
        return null;
    }
}
