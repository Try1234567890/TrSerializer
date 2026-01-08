package me.tr.trserializer.handlers.java;

import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.handlers.TypeHandler;
import me.tr.trserializer.logger.Logger;
import me.tr.trserializer.logger.ProcessLogger;
import me.tr.trserializer.registries.HandlersRegistry;
import me.tr.trserializer.types.GenericType;

import java.util.UUID;

public class UUIDHandler implements TypeHandler {


    @Override
    public UUID deserialize(Object obj, GenericType<?> type) {
        try {
            String value = HandlersRegistry.STRING_HANDLER.deserialize(obj, new GenericType<>(String.class));
            return UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            Logger.exception(
                    new TypeMissMatched("The provided object " + obj + " is not convertible to an UUID"));
            return null;
        }
    }

    @Override
    public String serialize(Object obj, GenericType<?> type) {
        if (obj instanceof UUID uuid) {
            return uuid.toString();
        } else {
            Logger.exception(
                    new TypeMissMatched("The provided object " + obj + " is not an UUID"));
            return null;
        }
    }
}
