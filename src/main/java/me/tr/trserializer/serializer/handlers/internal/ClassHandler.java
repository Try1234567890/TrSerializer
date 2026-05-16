package me.tr.trserializer.serializer.handlers.internal;

import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.exceptions.TranslationError;
import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.serializer.SerializerTask;
import me.tr.trserializer.serializer.handlers.SerializerHandler;

public class ClassHandler implements SerializerHandler {
    public static final ClassHandler INSTANCE = new ClassHandler();

    @Override
    public void serialize(SerializerTask task) throws TranslationError, TypeMissMatched {
        if (!(task.getObject() instanceof Class<?> cls)) return;

        task.getResult().accept(cls.getName());
    }

    @Override
    public boolean canHandle(SerializerTask task) {
        Object obj = task.getObject();
        return obj instanceof Class<?>;
    }

    @Override
    public DeserializerHandler getDeserializerHandler() {
        return null;
    }
}
