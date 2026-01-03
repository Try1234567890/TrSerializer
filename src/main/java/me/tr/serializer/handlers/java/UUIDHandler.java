package me.tr.serializer.handlers.java;

import me.tr.serializer.handlers.TypeHandler;
import me.tr.serializer.types.GenericType;

import java.util.UUID;

public class UUIDHandler implements TypeHandler {
    @Override
    public Object deserialize(Object obj, GenericType<?> type) {
        if (obj instanceof UUID uuid)
            return uuid;
        if (obj instanceof String str)
            return UUID.fromString(str);
        return null;
    }

    @Override
    public Object serialize(Object obj, GenericType<?> type) {
        if (obj instanceof UUID uuid)
            return uuid.toString();
        return null;
    }
}
