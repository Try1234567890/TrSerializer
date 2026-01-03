package me.tr.serializer.handlers.java;

import me.tr.serializer.exceptions.TypeMissMatched;
import me.tr.serializer.handlers.TypeHandler;
import me.tr.serializer.logger.TrLogger;
import me.tr.serializer.registries.HandlersRegistry;
import me.tr.serializer.types.GenericType;

import java.util.UUID;

public class UUIDHandler implements TypeHandler {


    @Override
    public UUID deserialize(Object obj, GenericType<?> type) {
        try {
            String value = HandlersRegistry.STRING_HANDLER.deserialize(obj, new GenericType<>(String.class));
            return UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            TrLogger.getInstance().exception(
                    new TypeMissMatched("The provided object " + obj + " is not convertible to an UUID"));
            return null;
        }
    }

    @Override
    public String serialize(Object obj, GenericType<?> type) {
        if (obj instanceof UUID uuid) {
            return uuid.toString();
        } else {
            TrLogger.getInstance().exception(
                    new TypeMissMatched("The provided object " + obj + " is not an UUID"));
            return null;
        }
    }
}
