package me.tr.trserializer.deserializer.handlers.internal;

import me.tr.trserializer.deserializer.DeserializerTask;
import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.exceptions.HandlerError;
import me.tr.trserializer.exceptions.TranslationError;
import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.serializer.handlers.SerializerHandler;
import me.tr.trserializer.serializer.handlers.internal.EnumSerializerHandler;
import me.tr.trserializer.types.GenericType;

public class EnumDeserializerHandler implements DeserializerHandler {
    public static final EnumDeserializerHandler INSTANCE = new EnumDeserializerHandler();

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void deserialize(DeserializerTask task) throws TranslationError, TypeMissMatched {
        if (!(task.getObject() instanceof String str)) return;
        Class<?> cls = task.getGenericType().getTypeClass();

        // TODO: An options to choose which method call.
        //       with support for a custom one.
        try {
            Object value = Enum.valueOf((Class<? extends Enum>) cls, str);
            task.getResult().accept(value);
        } catch (IllegalArgumentException ex) {
            throw new HandlerError("The constants with name " + str + " is not found in " + task.getGenericType(), ex);
        }

    }

    @Override
    public boolean canHandle(DeserializerTask task) {
        Object obj = task.getObject();
        GenericType<?> type = task.getGenericType();
        return type.isEnum() && obj instanceof String;
    }

    @Override
    public SerializerHandler getSerializerHandler() {
        return EnumSerializerHandler.INSTANCE;
    }
}
