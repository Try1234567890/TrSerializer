package me.tr.trserializer.deserializer.handlers.containers;

import me.tr.trserializer.deserializer.DeserializerTask;
import me.tr.trserializer.deserializer.handlers.DeserializerHandler;
import me.tr.trserializer.serializer.handlers.SerializerHandler;
import me.tr.trserializer.serializer.handlers.containers.OptionalSerializerHandler;
import me.tr.trserializer.types.GenericType;
import me.tr.trserializer.utility.Wrappers;

import java.util.Optional;

public class OptionalDeserializerHandler implements DeserializerHandler {
    public static final OptionalDeserializerHandler INSTANCE = new OptionalDeserializerHandler();

    @Override
    public void deserialize(DeserializerTask task) {
        Object rawObject = task.getObject();
        Class<?> expectedObjectType = task.getGenericType().getFirstArgumentClass();

        if (rawObject == null) {
            Optional<Object> result = Optional.empty();
            task.getResult().accept(result);
            return;
        }

        if (Wrappers.isAssignable(rawObject.getClass(), expectedObjectType)) {
            Optional<?> result = Optional.of(task.getObject());
            task.getResult().accept(result);
            return;
        }

        task.deserialize(rawObject, new GenericType<>(expectedObjectType), (o) -> task.deserialize(o, task.getGenericType(), task.getResult()));
    }

    @Override
    public boolean canHandle(DeserializerTask task) {
        GenericType<?> type = task.getGenericType();

        return type.is(Optional.class);
    }

    @Override
    public SerializerHandler getSerializerHandler() {
        return OptionalSerializerHandler.INSTANCE;
    }
}
