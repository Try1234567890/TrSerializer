package me.tr.trserializer.deserializer.helper.typeResolver;

import me.tr.trserializer.types.GenericType;

import java.lang.reflect.Field;

public class StaticDeserializerTypeResolver implements DeserializerTypeResolver {
    public static final StaticDeserializerTypeResolver INSTANCE = new StaticDeserializerTypeResolver();

    public static StaticDeserializerTypeResolver getInstance() {
        return INSTANCE;
    }

    public static GenericType<?> resolve(Field field) {
        return INSTANCE.getType(field);
    }

    @Override
    public GenericType<?> getType(Field field) {
        // TODO: Adding options to annotate
        //       field and customize the type.
        return new GenericType<>(field);
    }

}
