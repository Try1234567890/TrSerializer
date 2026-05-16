package me.tr.trserializer.serializer.helpers.typeResolver;

import me.tr.trserializer.serializer.SerializerTask;
import me.tr.trserializer.types.GenericType;

public final class SerializerTaskTypeResolver implements SerializerTypeResolver {
    private final ObjectSerializerTypeResolver typeResolver;

    public SerializerTaskTypeResolver(SerializerTask task) {
        if (task == null) throw new IllegalArgumentException("The task cannot be null.");

        this.typeResolver = new ObjectSerializerTypeResolver(task.getObject());
    }

    public ObjectSerializerTypeResolver getTypeResolver() {
        return typeResolver;
    }

    public GenericType<?> getType() {
        return getTypeResolver().getType();
    }

    @Override
    public GenericType<?> getType(Object obj) {
        return getTypeResolver().getType(obj);
    }


}













