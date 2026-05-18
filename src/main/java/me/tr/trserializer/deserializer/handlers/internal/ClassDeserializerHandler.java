package me.tr.trserializer.deserializer.handlers.internal;

import me.tr.trserializer.deserializer.DeserializerTask;
import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.exceptions.HandlerError;
import me.tr.trserializer.serializer.handlers.SerializerHandler;
import me.tr.trserializer.serializer.handlers.internal.ClassSerializerHandler;
import me.tr.trserializer.types.GenericType;

public class ClassDeserializerHandler implements DeserializerHandler {
    public static final ClassDeserializerHandler INSTANCE = new ClassDeserializerHandler();

    @Override
    public void deserialize(DeserializerTask task) {
        if (!(task.getObject() instanceof String str)) return;

        try {
            Class.forName(str, true, ClassLoader.getSystemClassLoader());
        } catch (ClassNotFoundException e) {
            throw new HandlerError("An error occurs while loading the class " + str, e);
        }
    }

    @Override
    public boolean canHandle(DeserializerTask task) {
        Object obj = task.getObject();
        GenericType<?> type = task.getGenericType();
        return type.is(Class.class) && obj instanceof String;
    }

    @Override
    public SerializerHandler getSerializerHandler() {
        return ClassSerializerHandler.INSTANCE;
    }
}
