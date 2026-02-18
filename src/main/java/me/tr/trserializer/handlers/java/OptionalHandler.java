package me.tr.trserializer.handlers.java;

import me.tr.trserializer.handlers.TypeHandler;
import me.tr.trserializer.types.GenericType;

import java.util.Optional;

public class OptionalHandler implements TypeHandler {

    @Override
    public Optional<?> deserialize(Object obj, GenericType<?> type) {
        if (obj instanceof Optional<?> optional) {
            return optional;
        }

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
