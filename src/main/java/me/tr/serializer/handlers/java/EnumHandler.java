package me.tr.serializer.handlers.java;

import me.tr.serializer.exceptions.TypeMissMatched;
import me.tr.serializer.handlers.TypeHandler;
import me.tr.serializer.logger.TrLogger;
import me.tr.serializer.types.GenericType;

public class EnumHandler implements TypeHandler {

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public Enum<?> deserialize(Object obj, GenericType<?> type) {
        Class<?> clazz = type.getTypeClass();

        if (!clazz.isEnum()) {
            TrLogger.getInstance().exception(
                    new TypeMissMatched("Class is not an enum"));
            return null;
        }

        String name = obj.toString();
        try {
            return Enum.valueOf((Class<Enum>) clazz, name);
        } catch (IllegalArgumentException e) {
            TrLogger.getInstance().exception(
                    new RuntimeException("Constant " + name + " not found in Enum " + clazz.getName(), e));
            return null;
        }
    }

    @Override
    public String serialize(Object obj, GenericType<?> type) {
        if (obj instanceof Enum<?> en)
            return en.name();

        TrLogger.getInstance().exception(
                new TypeMissMatched("The provided class is not an Enum, cannot serialize it."));
        return null;
    }
}
