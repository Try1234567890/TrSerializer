package me.tr.trserializer.serializer.helpers.typeResolver;

import me.tr.trserializer.types.GenericType;

/**
 * A serializer type resolver is a system that automatically
 * resolve the output object of a serializing process.
 */
public interface SerializerTypeResolver {

    /**
     * Get the generic type of the {@code object} output in serialization context.
     *
     *
     * @param obj The object.
     * @return The output generic type.
     */
    GenericType<?> getType(Object obj);

}
