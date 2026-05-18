package me.tr.trserializer.deserializer.helper.typeResolver;

import me.tr.trserializer.types.GenericType;

import java.lang.reflect.Field;

public interface DeserializerTypeResolver {

    GenericType<?> getType(Field field);

}
