package me.tr.serializer.handlers.java;

import me.tr.serializer.handlers.TypeHandler;
import me.tr.serializer.logger.TrLogger;
import me.tr.serializer.types.GenericType;

import java.util.Optional;

public class OptionalHandler implements TypeHandler {

    @Override
    public Optional<?> deserialize(Object obj, GenericType<?> type) {
        if (obj instanceof Optional<?> optional) {
            TrLogger.getInstance().warn("The provided object is already an optional, this will be returned.");
            return optional;
        }

        return Optional.ofNullable(obj);
    }

    @Override
    public Object serialize(Object obj, GenericType<?> type) {
        if (obj instanceof Optional<?> optional) {
            return optional.orElse(null);
        }

        TrLogger.getInstance().warn("The provided object is not an optional, but will be ignored.");
        return obj;
    }

}
