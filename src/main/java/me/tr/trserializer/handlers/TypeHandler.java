package me.tr.trserializer.handlers;

import me.tr.trserializer.types.GenericType;

public interface TypeHandler {

    Object deserialize(Object obj, GenericType<?> type);

    Object serialize(Object obj, GenericType<?> type);
}
