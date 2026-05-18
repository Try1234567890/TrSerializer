package me.tr.trserializer.deserializer.handlers.security;

import me.tr.trserializer.deserializer.DeserializerTask;
import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.exceptions.HandlerError;
import me.tr.trserializer.serializer.handlers.SerializerHandler;
import me.tr.trserializer.serializer.handlers.security.UUIDSerializerHandler;
import me.tr.trserializer.types.GenericType;

import java.util.UUID;

public class UUIDDeserializerHandler implements DeserializerHandler {
    public static final UUIDDeserializerHandler INSTANCE = new UUIDDeserializerHandler();

    @Override
    public void deserialize(DeserializerTask task) {
        if (!(task.getObject() instanceof String str)) return;
        try {
            task.getResult().accept(UUID.fromString(str));
        } catch (IllegalArgumentException ex) {
            throw new HandlerError("An error occurs while deserializing UUID", ex);
        }
    }

    @Override
    public boolean canHandle(DeserializerTask task) {
        Object object = task.getObject();
        GenericType<?> type = task.getGenericType();
        return type.is(UUID.class) && object instanceof String str &&
                (str.charAt(8) == '-' && str.charAt(13) == '-' && str.charAt(18) == '-' && str.charAt(23) == '-');
    }

    @Override
    public SerializerHandler getSerializerHandler() {
        return UUIDSerializerHandler.INSTANCE;
    }
}
