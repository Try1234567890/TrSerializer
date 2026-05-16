package me.tr.trserializer.serializer.handlers.security;

import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.exceptions.TranslationError;
import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.serializer.SerializerTask;
import me.tr.trserializer.serializer.handlers.SerializerHandler;

import java.security.PrivateKey;
import java.util.Base64;

public class PrivateKeyHandler implements SerializerHandler {
    public static final PrivateKeyHandler INSTANCE = new PrivateKeyHandler();

    @Override
    public void serialize(SerializerTask task) throws TranslationError, TypeMissMatched {
        if (!(task.getObject() instanceof PrivateKey privateKey)) return;

        // Converte la chiave in Base64
        String encodedKey = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        task.getResult().accept(encodedKey);
    }

    @Override
    public boolean canHandle(SerializerTask task) {
        return task.getObject() instanceof PrivateKey;
    }

    @Override
    public DeserializerHandler getDeserializerHandler() {
        return null;
    }
}