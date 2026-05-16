package me.tr.trserializer.serializer.helpers.typeResolver;

import me.tr.trserializer.types.GenericType;

public interface SerializerTypeResolver {

    /**
     * Get the generic type in serialization
     * context for the {@code object}.
     *
     * @param obj The object to consider.
     * @return The generic type
     */
    GenericType<?> getType(Object obj);

}
