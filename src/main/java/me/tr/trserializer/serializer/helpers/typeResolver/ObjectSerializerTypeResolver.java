package me.tr.trserializer.serializer.helpers.typeResolver;

import me.tr.trserializer.types.GenericType;

public final class ObjectSerializerTypeResolver implements SerializerTypeResolver {
    private final Object object;

    public ObjectSerializerTypeResolver(Object object) {
        this.object = object;
    }


    public GenericType<?> getType() {
        return getType(object);
    }

    @Override
    public GenericType<?> getType(Object obj) {
        return StaticObjectSerializerTypeResolver.resolve(obj);
    }
}













