package me.tr.trserializer.deserializer.helper.typeResolver;

import me.tr.trserializer.types.GenericType;

import java.lang.reflect.Field;

/**
 * A deserializer type resolver is a system that automatically
 * resolve the output object of a serializing process.
 * <p>
 * This implementation follow the singleton pattern and provide
 * an output type based only on the object that the deserializer
 * is processing.
 */
public class StaticDeserializerTypeResolver implements DeserializerTypeResolver {
    public static final StaticDeserializerTypeResolver INSTANCE = new StaticDeserializerTypeResolver();

    public static StaticDeserializerTypeResolver getInstance() {
        return INSTANCE;
    }

    public static GenericType<?> resolve(Field field) {
        return INSTANCE.getType(field);
    }

    /**
     * Get the generic type of the {@code object} output in deserialization context.
     *
     * @param field The field to get type from.
     * @return The output generic type.
     */
    @Override
    public GenericType<?> getType(Field field) {
        // TODO: Adding options to annotate
        //       field and customize the type.
        return new GenericType<>(field);
    }

}
