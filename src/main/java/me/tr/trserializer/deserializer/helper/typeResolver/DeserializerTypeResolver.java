package me.tr.trserializer.deserializer.helper.typeResolver;

import me.tr.trserializer.types.GenericType;

import java.lang.reflect.Field;

/**
 * A deserializer type resolver is a system that automatically
 * resolve the output object of a deserializing process.
 */
public interface DeserializerTypeResolver {

    /**
     * Get the generic type of the {@code object} output in deserialization context.
     *
     * @param field The field to get type from.
     * @return The output generic type.
     */
    GenericType<?> getType(Field field);

}
