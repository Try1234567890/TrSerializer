package me.tr.trserializer.handlers.java;

import me.tr.trserializer.exceptions.TypeMissMatched;
import me.tr.trserializer.handlers.TypeHandler;
import me.tr.trserializer.instancers.ProcessInstancer;
import me.tr.trserializer.logger.TrLogger;
import me.tr.trserializer.types.GenericType;
import me.tr.trserializer.utility.Utility;

import java.lang.reflect.InvocationTargetException;

public class EnumHandler implements TypeHandler {

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public Enum<?> deserialize(Object obj, GenericType<?> type) {
        Class<?> clazz = type.getTypeClass();

        ProcessInstancer.getMethodByAnnotation(clazz).ifPresent(method -> {
            try {
                method.setAccessible(true);
                method.invoke(null, obj);
            } catch (IllegalAccessException | InvocationTargetException e) {
                TrLogger.exception(new RuntimeException(
                        "An error occurs while creating a new instance of " + type.getTypeClass(), e));
            }
        });

        String name = obj.toString();
        try {
            return Enum.valueOf((Class<Enum>) clazz, name);
        } catch (IllegalArgumentException e) {
            TrLogger.exception(
                    new RuntimeException("Constant " + name + " not found in Enum " + Utility.getClassName(clazz), e));
            return null;
        }
    }

    @Override
    public String serialize(Object obj, GenericType<?> type) {
        if (obj instanceof Enum<?> en)
            return en.name();

        TrLogger.exception(
                new TypeMissMatched("The provided class is not an Enum, cannot serialize it."));
        return null;
    }
}
