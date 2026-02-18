package me.tr.trserializer.handlers;

import me.tr.trserializer.exceptions.ProcessError;
import me.tr.trserializer.types.GenericType;

public interface TypeHandler {

    Object deserialize(Object obj, GenericType<?> type) throws ProcessError;

    Object serialize(Object obj, GenericType<?> type) throws ProcessError;
}
