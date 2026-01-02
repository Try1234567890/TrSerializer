package me.tr.serializer.handlers;

import me.tr.serializer.types.GenericType;

public interface TypeHandler {

    Object deserialize(Object obj, GenericType<?> type);

    Object serialize(Object obj, GenericType<?> type);
}
