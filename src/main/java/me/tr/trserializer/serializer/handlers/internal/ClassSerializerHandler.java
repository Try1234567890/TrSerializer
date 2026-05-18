package me.tr.trserializer.serializer.handlers.internal;

import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.deserializer.handlers.internal.ClassDeserializerHandler;
import me.tr.trserializer.exceptions.TranslationError;
import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.serializer.SerializerTask;
import me.tr.trserializer.serializer.handlers.SerializerHandler;

public class ClassSerializerHandler implements SerializerHandler {
    public static final ClassSerializerHandler INSTANCE = new ClassSerializerHandler();

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
        return ClassDeserializerHandler.INSTANCE;
    }
}
