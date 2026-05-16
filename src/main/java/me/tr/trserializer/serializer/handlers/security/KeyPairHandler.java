package me.tr.trserializer.serializer.handlers.security;

import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.exceptions.TranslationError;
import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.serializer.SerializerTask;
import me.tr.trserializer.serializer.handlers.SerializerHandler;

import java.security.KeyPair;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class KeyPairHandler implements SerializerHandler {
    public static final KeyPairHandler INSTANCE = new KeyPairHandler();

    @Override
    public void serialize(SerializerTask task) throws TranslationError, TypeMissMatched {
        if (!(task.getObject() instanceof KeyPair keyPair)) return;

        CompletableFuture<Object> publicFuture = new CompletableFuture<>();
        CompletableFuture<Object> privateFuture = new CompletableFuture<>();

        publicFuture.thenAcceptBoth(privateFuture, (pub, priv) -> {
            Map.Entry<Object, Object> serializedKeyPair = Map.entry(pub, priv);
            task.getResult().accept(serializedKeyPair);
        });


        task.serialize(keyPair.getPublic(), publicFuture::complete);
        task.serialize(keyPair.getPrivate(), privateFuture::complete);
    }

    @Override
    public boolean canHandle(SerializerTask task) {
        return task.getObject() instanceof KeyPair;
    }

    @Override
    public DeserializerHandler getDeserializerHandler() {
        return null;
    }
}