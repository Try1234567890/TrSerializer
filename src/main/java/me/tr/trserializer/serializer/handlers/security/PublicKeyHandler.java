package me.tr.trserializer.serializer.handlers.security;

import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.exceptions.TranslationError;
import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.serializer.SerializerTask;
import me.tr.trserializer.serializer.handlers.SerializerHandler;

import java.security.PublicKey;
import java.util.Base64;

public class PublicKeyHandler implements SerializerHandler {
    public static final PublicKeyHandler INSTANCE = new PublicKeyHandler();

    @Override
    public void serialize(SerializerTask task) throws TranslationError, TypeMissMatched {
        if (!(task.getObject() instanceof PublicKey publicKey)) return;

        // Converte la chiave in Base64
        String encodedKey = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        task.getResult().accept(encodedKey);
    }

    @Override
    public boolean canHandle(SerializerTask task) {
        return task.getObject() instanceof PublicKey;
    }

    @Override
    public DeserializerHandler getDeserializerHandler() {
        return null;
    }
}