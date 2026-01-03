package me.tr.serializer.handlers.java;

import me.tr.serializer.handlers.TypeHandler;
import me.tr.serializer.types.GenericType;

import java.util.Optional;

public class OptionalHandler implements TypeHandler {

    @Override
    public Optional<?> deserialize(Object obj, GenericType<?> type) {
        return Optional.ofNullable(obj);
    }

    @Override
    public Object serialize(Object obj, GenericType<?> type) {
        if (obj instanceof Optional<?> optional) {
            return optional.orElse(null);
        }
        return obj;
    }

}
