package me.tr.serializer.handlers.java;

import me.tr.serializer.handlers.TypeHandler;
import me.tr.serializer.types.GenericType;

public class EnumHandler implements TypeHandler {

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public Enum<?> deserialize(Object obj, GenericType<?> type) {
        Class<?> clazz = type.getClazz();
        if (!clazz.isEnum())
            throw new ClassCastException("Class is not an enum");

        String name = obj.toString();
        try {
            return Enum.valueOf((Class<Enum>) clazz, name);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Constant " + name + " not found in Enum " + clazz.getName());
        }
    }

    @Override
    public Object serialize(Object obj, GenericType<?> type) {
        if (obj instanceof Enum<?> en) {
            return en.name();
        } else throw new ClassCastException("The provided class is not an Enum, cannot serialize it.");
    }
}
